package com.proxy.forge.tools;


import com.alibaba.fastjson2.JSONObject;
import com.google.common.net.InternetDomainName;
import com.proxy.forge.api.pojo.FingerprintAnalysisReuslt;
import com.proxy.forge.dto.GlobalSettings;
import com.proxy.forge.dto.WebSite;
import com.proxy.forge.service.IpInfoService;
import com.proxy.forge.vo.fingerprint.Battery;
import com.proxy.forge.vo.fingerprint.ClientFingerprint;
import com.proxy.forge.vo.fingerprint.Gyroscope;
import com.proxy.forge.vo.ipinfo.ClientIpInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.proxy.forge.tools.GlobalStaticVariable.REDIS_WEBSITE_CACHE_KEY;

/**
 * [Refactored] 指纹分析工具类，用于分析设备指纹信息。
 * 包含IP策略检查和设备指纹详细分析。
 */

@Slf4j
@Component
public class FingerprintAnalysisUtil {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    IpInfoService ipInfoService;

    // ===================================================================
    // 高级验证的辅助检查方法
    // ===================================================================

    private boolean isSuspiciousBatteryState(ClientFingerprint clientFingerprint) {
        Battery battery = clientFingerprint.getPlat().getBattery();
        if (battery == null) {
            log.debug("验证项检查 [电池]: 未找到电池信息。");
            return false;
        }

        boolean isCharging = battery.getCharging();
        if (isCharging && battery.getLevel() >= 1.0) {
            log.warn("[反作弊]: 检测到可疑充电状态 (电量: {}, 正在充电: {})", battery.getLevel(), true);
            return true;
        }
        log.info("验证项检查 [电池]: 电池状态正常。是否充电：{}'电量：{}", battery.getCharging(), battery.getLevel());
        return false;
    }

    private boolean isIOSMobileUserAgent(ClientFingerprint clientFingerprint) {
        String ua = clientFingerprint.getPlat().getUserAgent().toLowerCase();
        if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ipod")) {
            return true;
        }
        return false;
    }

    private boolean isAndroidMobileUserAgent(ClientFingerprint clientFingerprint) {
        String ua = clientFingerprint.getPlat().getUserAgent().toLowerCase();
        return ua.contains("android");
    }

    // 检查桌面级GPU
    private boolean isDesktopGpu(ClientFingerprint clientFingerprint) {
        String renderer = clientFingerprint.getWebgl().getRenderer();
        String vendor = clientFingerprint.getWebgl().getVendor();
        String[] desktopGpuKeywords = {"nvidia", "geforce", "rtx", "quadro", "amd", "radeon", "intel(r) hd", "intel(r) iris"};
        for (String keyword : desktopGpuKeywords) {
            if (renderer.contains(keyword) || vendor.contains(keyword)) {
                log.warn("验证失败 [反作弊]: 检测到桌面级GPU。关键字: '{}', Renderer: '{}', Vendor: '{}'", keyword, renderer, vendor);
                return true;
            }
        }
        return false;
    }

    // 是否是桌面平台 PC
    private boolean isDesktopPlatform(ClientFingerprint clientFingerprint) {
        if (clientFingerprint.getPlat().getPlatform().toLowerCase().contains("win32")
                || clientFingerprint.getPlat().getPlatform().toLowerCase().contains("macintel")
        ) {
            log.warn("验证失败 [反作弊]: 平台标识为'{}'。", clientFingerprint.getPlat().getPlatform());
            return true;
        }
        return false;
    }

    // 陀螺仪数据是否正确
    private boolean isSensorDataValid(ClientFingerprint clientFingerprint) {
        List<Gyroscope> gyroscopeList = clientFingerprint.getGyroscope();
        if (gyroscopeList == null) {
            log.debug("验证项检查 [传感器]: 未提供gyroscope字段，跳过检查。");
            return true;
        }
        if (gyroscopeList.isEmpty()) {
            log.debug("验证项检查 [传感器]: gyroscope列表为空，跳过检查。");
            return true;
        }
        Object firstEntryObj = gyroscopeList.get(0);
        if (!(firstEntryObj instanceof Map<?, ?> firstEntryMap)) {
            log.warn("验证失败 [反作弊]: gyroscope列表的第一项不是一个Map对象。");
            return false;
        }
        Object dataObj = firstEntryMap.get("data");
        if (dataObj == null) {
            log.warn("验证失败 [反作弊]: gyroscope第一条记录的'data'字段为null。");
            return false;
        }
        if (!(dataObj instanceof Map<?, ?> dataMap)) {
            log.warn("验证失败 [反作弊]: gyroscope的'data'字段不是一个Map对象，类型为'{}'。", dataObj.getClass().getSimpleName());
            return false;
        }

        if (dataMap.isEmpty()) {
            log.warn("验证失败 [反作弊]: gyroscope的'data'对象为空。");
            return false;
        }
        if (dataMap.get("alpha") == null) {
            log.warn("验证失败 [反作弊]: gyroscope的'data'对象中'alpha'值为null，疑似无效读数。");
            return false;
        }
        log.info("验证项检查 [传感器]: 传感器数据格式和内容均有效。");
        return true;
    }

    /**
     * 分析提供的参数并返回包含分析结果的映射。
     *
     * @param serverName        待分析服务器名称。
     * @param clientFingerprint 客户端指纹信息。
     * @param clientIp          客户端的IP地址。
     * @param globalSettings    全局设置对象，包含各种配置参数。
     * @return 一个哈希图，其中键为字符串，值为对象，表示分析结果。
     */
    public FingerprintAnalysisReuslt analyze(String serverName, ClientFingerprint clientFingerprint, String clientIp, GlobalSettings globalSettings) {
        FingerprintAnalysisReuslt fingerprintAnalysisReuslt = new FingerprintAnalysisReuslt();
        // 查找数据库是否有匹配的 代理网站列表, 没有的话， 直接跳转走。状态是可用状态
        String websiteConfig = stringRedisTemplate.opsForValue().get(REDIS_WEBSITE_CACHE_KEY + serverName);
        if (StringUtils.isBlank(websiteConfig)) {
            InternetDomainName idn = InternetDomainName.from(serverName);
            if (idn.isUnderPublicSuffix()) {
                websiteConfig = stringRedisTemplate.opsForValue().get(REDIS_WEBSITE_CACHE_KEY + "*." + idn.topPrivateDomain());
            }
        }
        if (StringUtils.isBlank(websiteConfig)) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [域名未配置]: 找不到转发站点.");
            return fingerprintAnalysisReuslt;
        }

        // 读取站点的配置文件
        WebSite webSite = JSONObject.parseObject(websiteConfig, WebSite.class);
        if (!webSite.getStatus().equalsIgnoreCase("running")) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [域名已配置]: 状态停止,不处理该请求.");
            return fingerprintAnalysisReuslt;
        }

        // 检查传感器 只检查安卓端
        if (webSite.getCheckSensor() && isAndroidMobileUserAgent(clientFingerprint) && !isSensorDataValid(clientFingerprint)) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [传感器开关开打开]: 且 传感器检查无效.");
            return fingerprintAnalysisReuslt;
        }
        // 不允许pc访问的时候检查 平台， 如果是pc就拦击
        if (!webSite.getAllowPc() && webSite.getCheckPlatform() && isDesktopPlatform(clientFingerprint)) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [不允许PC访问]: 且 匹配到PC平台 .");
            return fingerprintAnalysisReuslt;
        }

        // 是否桌面级显卡, 不允许PC 且检查到 PC特征
        if (!webSite.getAllowPc() && isDesktopGpu(clientFingerprint)) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [不允许PC访问]: 且 找到桌面级显卡特征 .");
            return fingerprintAnalysisReuslt;
        }
        // 不允许 安卓或ios 访问
        if (!webSite.getAllowMobile() && (isAndroidMobileUserAgent(clientFingerprint) || isIOSMobileUserAgent(clientFingerprint))) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [不允许移动端访问]: 且 找到移动UA特征 .");
            return fingerprintAnalysisReuslt;
        }
        // 允许移动端 不允许安卓访问
        if (webSite.getAllowMobile() && !webSite.getAllowAndroid() && isAndroidMobileUserAgent(clientFingerprint)) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [允许移动端访问不允许安卓访问]: 且 找到安卓特征 .");
            return fingerprintAnalysisReuslt;
        }
        //允许移动端 不允许ios访问
        if (webSite.getAllowMobile() && !webSite.getAllowIos() && isIOSMobileUserAgent(clientFingerprint)) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [允许移动端访问不允许IOS访问]: 且 找到IOS特征 .");
            return fingerprintAnalysisReuslt;
        }

        // 是否检查满电状态充电(PC端 基本都是满电充电中)
        if (webSite.getCheckBatteryCharging() && isSuspiciousBatteryState(clientFingerprint)) {
            if (isIOSMobileUserAgent(clientFingerprint) || isAndroidMobileUserAgent(clientFingerprint)) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [满电充电检查]: 开启且校验移动端符合.");
                return fingerprintAnalysisReuslt;
            }
        }

        // 检查是否自动化
        if (webSite.getCheckAutomation() && clientFingerprint.getPlat().isWebdriver()) {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝 [自动化检查]: 条件符合,拦截 .");
            return fingerprintAnalysisReuslt;
        }

        // ======================== 开始检查终端的网络环境信息 ==============================
        ClientIpInfo clientIpInfo = ipInfoService.getIpregistryCoDetails(clientIp);
        if (clientIpInfo != null) {
            log.info("[终端ip信息] : [{}], 主机名: [{}], 国家: [{}], 洲/省: [{}], 城市: [{}]", clientIp, serverName, clientIpInfo.getLocation().getCountry().getCode(), clientIpInfo.getLocation().getRegion().getName(), clientIpInfo.getLocation().getCity());
            if (StringUtils.isNotBlank(webSite.getAllowCountries()) && !webSite.getAllowCountries().contains(clientIpInfo.getLocation().getCountry().getCode())) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [IP归属策略]: IP不允许的国家访问: " + clientIpInfo.getLocation().getCountry().getCode());
                return fingerprintAnalysisReuslt;
            }

            // ip滥用
            if (webSite.getIsAbuser() && clientIpInfo.getSecurity().isAbuser()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [滥用者]: IP 被识别为滥用行为来源 (如垃圾邮件)。");
                return fingerprintAnalysisReuslt;
            }

            // 攻击者IP
            if (webSite.getIsAttacker() && clientIpInfo.getSecurity().isAttacker()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [攻击者]: IP 被识别为攻击者来源。");
                return fingerprintAnalysisReuslt;
            }
            // 保留地址
            if (webSite.getIsBogon() && clientIpInfo.getSecurity().isBogon()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [Bogon IP]: IP 是一个不应出现在公共互联网的保留地址。");
                return fingerprintAnalysisReuslt;
            }
            // 云服务商
            if (webSite.getIsCloudProvider() && clientIpInfo.getSecurity().isCloudProvider()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [云服务商]: IP来源于数据中心/云服务商。");
                return fingerprintAnalysisReuslt;
            }
            // 代理ip
            if (webSite.getIsProxy() && clientIpInfo.getSecurity().isProxy()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [代理]: IP 被识别为代理服务器。");
                return fingerprintAnalysisReuslt;
            }

            // 中继ip
            if (webSite.getIsRelay() && clientIpInfo.getSecurity().isRelay()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [中继]: IP 被识别为中继节点。");
                return fingerprintAnalysisReuslt;
            }

            //tor网络
            if (webSite.getIsTor() && clientIpInfo.getSecurity().isTor()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [Tor网络]: IP 是Tor网络的一部分。");
                return fingerprintAnalysisReuslt;
            }

            // tor网络出口节点
            if (webSite.getIsTorExit() && clientIpInfo.getSecurity().isTorExit()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [Tor出口节点]: IP 是一个Tor网络的出口节点。");
                return fingerprintAnalysisReuslt;
            }

            // vpn 节点
            if (webSite.getIsVpn() && clientIpInfo.getSecurity().isVpn()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [VPN]: IP 被识别为VPN服务。");
                return fingerprintAnalysisReuslt;
            }

            // 匿名
            if (webSite.getIsAnonymous() && clientIpInfo.getSecurity().isAnonymous()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [匿名]: IP 被识别为匿名来源。");
                return fingerprintAnalysisReuslt;
            }

            // 威胁
            if (webSite.getIsThreat() && clientIpInfo.getSecurity().isThreat()) {
                fingerprintAnalysisReuslt.setResult(false);
                fingerprintAnalysisReuslt.setMessage("策略拒绝 [威胁情报]: IP 被标记为常规威胁源。");
                return fingerprintAnalysisReuslt;
            }
        } else {
            fingerprintAnalysisReuslt.setResult(false);
            fingerprintAnalysisReuslt.setMessage("策略拒绝, [无可用key]: 请新增后使用.");
            log.error("[ip信息获取失败]: 无法获取ip信息.");
            return fingerprintAnalysisReuslt;
        }
        // 所有检查完毕，放行。
        fingerprintAnalysisReuslt.setResult(true);
        fingerprintAnalysisReuslt.setMessage("[策略通过]: 所有检查通过~");
        return fingerprintAnalysisReuslt;
    }
}

