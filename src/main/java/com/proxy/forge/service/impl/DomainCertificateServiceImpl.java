package com.proxy.forge.service.impl;

import com.proxy.forge.service.DomainCertificateService;
import com.proxy.forge.tools.CertificateManagement;
import com.proxy.forge.tools.DNSUtils;
import com.proxy.forge.vo.ResponseApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 域名证书实现类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-20 21:36
 **/
@Service
public class DomainCertificateServiceImpl implements DomainCertificateService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     *
     * @param domain：创建证书请求的域。
     * @param authType         用于证书请求的认证类型。
     * @return 表示证书请求结果或状态的对象。
     */
    @Override
    public Object createCertificateRequest(String domain, CertificateManagement.AuthType authType) {
        ResponseApi responseApi;
        try {
            CertificateManagement.OrderResult order = CertificateManagement.createOrder(domain, authType);
            if (authType == CertificateManagement.AuthType.DNS) {
                responseApi = new ResponseApi(200, "请添加域名: [" + order.token + "] TXT 记录值: " + order.authorization, order.token);
            } else if (StringUtils.isNotBlank(order.taskId) && StringUtils.isNotBlank(order.token) && StringUtils.isNotBlank(order.authorization)) {
                stringRedisTemplate.opsForValue().set("certChalleng:" + order.token, order.authorization, 60 * 10,
                        TimeUnit.SECONDS);
                responseApi = new ResponseApi(200, "已提交申请，请手动按钮验证...", order.token);
            } else {
                responseApi = new ResponseApi(500, "fail", null);
            }
        } catch (Exception e) {
            responseApi = new ResponseApi(500, e.getMessage(), null);
        }
        return responseApi;
    }

    /**
     * 创建证书检查请求。
     *
     * @param token 用于标识特定证书订单的令牌。
     * @return 返回一个ResponseApi对象，包含状态码、消息和可能的数据。根据检查结果的状态，返回不同的状态码：
     * - 200: 表示成功且已完成。
     * - 201: 表示仍在处理中。
     * - 202: 表示已完成但不成功。
     * - 500: 表示发生异常情况。
     */
    @Override
    public Object createCertificateChcekRequest(String token) {
        ResponseApi responseApi;
        try {
            CertificateManagement.CheckResult checkResult = CertificateManagement.checkOrder(token);
            if (checkResult.success && checkResult.done) {
                responseApi = new ResponseApi(200, checkResult.message, null);
            } else if (checkResult.done && !checkResult.success) {
                responseApi = new ResponseApi(202, checkResult.message, null);
            } else {
                responseApi = new ResponseApi(201, checkResult.message, null);
            }
        } catch (Exception e) {
            responseApi = new ResponseApi(500, e.getMessage(), null);
        }
        return responseApi;
    }
}
