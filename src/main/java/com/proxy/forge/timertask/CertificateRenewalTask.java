package com.proxy.forge.timertask;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.api.pojo.SaveDomain;
import com.proxy.forge.dto.Domain;
import com.proxy.forge.dto.GlobalSettings;
import com.proxy.forge.service.DomainService;
import com.proxy.forge.service.GlobalSettingService;
import com.proxy.forge.tools.CertificateManagement;
import com.proxy.forge.vo.ResponseApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.timertask</p>
 * <p>Description: 证书续签任务</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-04 13:45
 **/
@Slf4j
@Component
public class CertificateRenewalTask {

    @Autowired
    DomainService domainService;
    @Autowired
    GlobalSettingService globalSettingService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0 2 * * ?")
    public void startTask() throws Exception {
        GlobalSettings globalSetting = globalSettingService.getGlobalSetting();
        if (globalSetting.getAutoRenew()) {
            ResponseApi responseApi = (ResponseApi) domainService.domainAll();
            List<Domain> domains = JSON.parseArray(JSONObject.toJSONString(responseApi.getData()), Domain.class);
            for (Domain domain : domains) {
                if (domain.isEnableSsl()) {
                    log.info("[域名自动续签]  域名: {} , 过期时间: {}, 是否满足 {} 天到期续签条件: {}, 剩余过期时间: {}", domain.getDomain()
                            , domain.getCertExpTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            , globalSetting.getRenewDays()
                            , isFutureWithinDays(domain.getCertExpTime(), globalSetting.getRenewDays())
                            , getRemainingTime(domain.getCertExpTime())
                    );
                    if (isFutureWithinDays(domain.getCertExpTime(), globalSetting.getRenewDays())) {
                        // 开始自动续签流程 1.申请证书.
                        CertificateManagement.AuthType authType = CertificateManagement.AuthType.fromCode(domain.getMethod());
                        CertificateManagement.OrderResult order = CertificateManagement.createOrder(domain.getDomain(), authType);

                        if (authType == CertificateManagement.AuthType.DNS) {
                            // 正常 泛解析，dns txt 记录 不会每次申请都变 如果变动就无法处理了。
                        } else if (StringUtils.isNotBlank(order.taskId) && StringUtils.isNotBlank(order.token) && StringUtils.isNotBlank(order.authorization)) {
                            stringRedisTemplate.opsForValue().set("certChalleng:" + order.token, order.authorization, 60 * 10,
                                    TimeUnit.SECONDS);
                        } else {
                            SaveDomain saveDomain = new SaveDomain();
                            saveDomain.setRemark("自动续签订单创建失败.");
                            saveDomain.setDomain(domain.getDomain());
                            saveDomain.setSsl(domain.isEnableSsl());
                            saveDomain.setStatus(domain.getStatus());
                            domain.setMethod(domain.getMethod());
                            domainService.saveDomain(saveDomain);
                            log.info("[域名自动续签]  域名: {},自动续签订单创建失败.", domain.getDomain());
                            break;
                        }

                        // 2.验证订单. 验证90次。
                        for (int i = 0; i < 90; i++) {
                            CertificateManagement.CheckResult checkResult = CertificateManagement.checkOrder(order.token);
                            if (checkResult.done && checkResult.success) {
                                // 证书续签成功.
                                SaveDomain saveDomain = new SaveDomain();
                                saveDomain.setRemark("域名证书续签成功." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                saveDomain.setDomain(domain.getDomain());
                                saveDomain.setSsl(domain.isEnableSsl());
                                saveDomain.setStatus(domain.getStatus());
                                domain.setMethod(domain.getMethod());
                                domainService.saveDomain(saveDomain);
                                break;
                            } else {
                                log.info("[域名自动续签] 域名: [{}],  验证订单状态: [{}]", domain.getDomain(), checkResult.message);
                                Thread.sleep(5000);
                            }
                        }
                    }
                }
            }
        } else {
            log.info("[域名自动续签]   配置未启用,不自动续签.");
        }
    }

    // 判断 某个时间是否在 大于现在，并且小于 day 天数
    public boolean isFutureWithinDays(LocalDateTime target, int day) {
        LocalDateTime now = LocalDateTime.now();
        long days = Duration.between(now, target).toDays();
        return days < day;
    }

    public String getRemainingTime(LocalDateTime targetTime) {
        LocalDateTime now = LocalDateTime.now();

        if (targetTime.isBefore(now)) {
            return "已过期";
        }

        Duration duration = Duration.between(now, targetTime);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return String.format("%d天 %d小时 %d分钟 %d秒", days, hours, minutes, seconds);
    }
}
