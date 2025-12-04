package com.proxy.forge.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.api.pojo.SaveApiKey;
import com.proxy.forge.dto.ApiKeys;
import com.proxy.forge.service.ApiKeysService;
import com.proxy.forge.service.IpInfoService;
import com.proxy.forge.tools.HttpUtils;
import com.proxy.forge.vo.ipinfo.ClientIpInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * IPINFO服务
 */
@Slf4j
@Service
public class IpInfoServiceImpl implements IpInfoService {

    @Autowired
    private RestClient.Builder restClientBuilder;
    @Autowired
    private ApiKeysService apiKeysService;

    // ---  Provider 名称 ---
//    private static final String IPINFO_PROVIDER_NAME = "ipinfo.io";
    private static final String IPREGISTRY_PROVIDER_NAME = "ipregistry.co";

    private static final String IPREGISTRY_API_URL = "https://api.ipregistry.co/%s?key=%s";
//    private static final String IPINFO_API_URL = "https://ipinfo.io/{ip}?token={token}";
    private static final List<String> PRIVATE_IP_PREFIXES = Arrays.asList(
            "127.0.0.1", "0:0:0:0:0:0:0:1", "10.", "172.16.", "172.17.", "172.18.",
            "172.19.", "172.20.", "172.21.", "172.22.", "172.23.", "172.24.",
            "172.25.", "172.26.", "172.27.", "172.28.", "172.29.", "172.30.",
            "172.31.", "192.168."
    );

    /**
     * 从ipregistry.co获取ip信息
     *
     * @param ipAddress 待查询的IP地址字符串
     * @return 返回一个完整的ClientIpInfo 对象
     */
    @Override
    public ClientIpInfo getIpregistryCoDetails(String ipAddress) {
        // 从 Service 获取所有可用 Key 的快照列表
        List<ApiKeys> availableKeys = apiKeysService.getCachedActiveKeys(IPREGISTRY_PROVIDER_NAME);
        Collections.shuffle(availableKeys);
        log.info("获取到 {} 个可用的 Key (Provider: {})，开始尝试...", availableKeys.size(), IPREGISTRY_PROVIDER_NAME);
        if (availableKeys.isEmpty() || isPrivateIp(ipAddress)) {
            return null;
        }
        // 2. 遍历快照列表进行重试
        for (ApiKeys currentKey : availableKeys) {
            try {
                // 3. 尝试 API 调用
                log.info("正在使用 Key ID: {} 查询 IP: {}", currentKey.getId(), ipAddress);
                HashMap<String, Object> header = new HashMap<>();
                byte[] res = HttpUtils.sendGetRequest(IPREGISTRY_API_URL.formatted(ipAddress, currentKey.getApiKey()), header, null);
                // 在处理“成功”逻辑前，必须检查 HTTP 状态码
                HttpResponse response = (HttpResponse) header.get("response");
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode < 200 || statusCode >= 300) {
                    // 这是一个 HTTP 错误 (例如 400, 401, 403, 500)
                    log.warn("Key ID: {} 查询 IP: {} 时返回 HTTP 错误码: {}", currentKey.getId(), ipAddress, statusCode);
                    // 仅在 401/403 (Key 本身的问题) 时停用
                    if (statusCode == HttpStatus.UNAUTHORIZED.value() || statusCode == HttpStatus.FORBIDDEN.value()) {
                        SaveApiKey saveApiKey = new SaveApiKey();
                        saveApiKey.setProvider(currentKey.getProvider());
                        saveApiKey.setId(currentKey.getId());
                        saveApiKey.setIsActive(false);
                        saveApiKey.setNotes("API认证失败 (" + statusCode + ")");
                        apiKeysService.saveApiKey(saveApiKey);
                    }
                    continue;
                }
                // 4. 只有 2xx 状态码会到达这里
                long remaining = 0;
                if (response.getHeaders("ipregistry-credits-remaining").length > 0) {
                    try {
                        remaining = Long.parseLong(response.getHeaders("ipregistry-credits-remaining")[0].getValue());
                    } catch (NumberFormatException e) {
                        log.warn("无法解析 Key ID: {} 的 'ipregistry-credits-remaining' 响应头", currentKey.getId());
                    }
                }
                SaveApiKey saveApiKey = new SaveApiKey();
                saveApiKey.setId(currentKey.getId());
                saveApiKey.setProvider(currentKey.getProvider());
                saveApiKey.setCreditsRemaining(remaining);
                // 点数小于100的时候  直接禁用该数据
                if (remaining < 100) {
                    saveApiKey.setIsActive(false);
                }
                apiKeysService.saveApiKey(saveApiKey);
                log.info("成功从 ipregistry.co 获取到IP信息: IP={}", ipAddress);
                // [成功] 返回结果并退出循环
                return JSONObject.parseObject(new String(res), ClientIpInfo.class);
            } catch (Exception e) {
                // [失败] 捕获其他所有异常 (如 HttpUtils 内部抛出的超时)
                log.error("Key ID: {} 查询时发生意外错误: {}", currentKey.getId(), e.getMessage());
            }
        }
        // 7. [最终失败] 如果循环结束仍未返回，说明所有 Key 均失败
        log.error("在 {} 次尝试后 (Provider: {}), 仍未能成功获取 IP {} 的信息。", availableKeys.size(), IPREGISTRY_PROVIDER_NAME, ipAddress);
        return null;
    }

    private boolean isPrivateIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) return true;
        for (String prefix : PRIVATE_IP_PREFIXES) {
            if (ipAddress.startsWith(prefix)) return true;
        }
        return false;
    }
}