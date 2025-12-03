package com.proxy.forge.service;

import com.proxy.forge.tools.CertificateManagement;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 域名证书服务类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-20 21:35
 **/

public interface DomainCertificateService {

    /**
     * 使用指定认证类型为指定域创建证书请求。
     *
     * @param domain：创建证书请求的域。
     * @param authType         用于证书请求的认证类型。
     * @return 表示证书请求结果或状态的对象。
     */
    Object createCertificateRequest(String domain, CertificateManagement.AuthType authType);

    /**
     * 创建证书检查请求。
     *
     * @param token 用于标识待检查的证书请求。
     * @return 表示证书检查请求结果或状态的对象。
     * - 200: 表示成功且已完成。
     * - 201: 表示仍在处理中。
     * - 202: 表示已完成但不成功,需要继续请求验证
     * - 500: 表示发生异常情况。
     */
    Object createCertificateChcekRequest(String token);
}
