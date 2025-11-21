package com.proxy.forge.controller;

import com.proxy.forge.api.pojo.DomainCertRequest;
import com.proxy.forge.service.DomainCertificateService;
import com.proxy.forge.tools.CertificateManagement;
import com.proxy.forge.vo.ResponseApi;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.controller</p>
 * <p>Description: 后台管理控制器</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-20 21:31
 **/
@RestController
@RequestMapping(value = "/pfadmin")
public class ManagerController {

    @Autowired
    DomainCertificateService domainCertificateService;

    /**
     * 处理证书请求的创建。
     *
     * @param domainCertRequest 包含域名和其他必要信息的请求体，用于创建证书请求。
     * @param request           服务器接收到的HTTP请求对象。
     * @param response          服务器发送回客户端的HTTP响应对象。
     * @return 返回表示证书请求结果或状态的对象。返回任务ID 然后 继续调用查询接口
     */
    @RequestMapping(value = "/certrequest")
    Object createCertRequest(@RequestBody @Validated DomainCertRequest domainCertRequest, HttpServletRequest request, HttpServletRequest response) {
        try {
            CertificateManagement.AuthType authType = CertificateManagement.AuthType.fromCode(domainCertRequest.getMethod());
            return domainCertificateService.createCertificateRequest(domainCertRequest.getDomain(), authType);
        } catch (Exception e) {
            return new ResponseApi(500, e.getMessage(), null);
        }
    }

    /**
     * 创建证书检查请求。
     *
     * @param domainCertRequest 包含待检查证书请求的令牌等信息的请求体。
     * @param request           服务器接收到的HTTP请求对象。
     * @param response          服务器发送回客户端的HTTP响应对象。
     * @return 返回表示证书检查请求结果或状态的对象。可能的返回值包括：
     * - 200: 表示成功且已完成。
     * - 201: 表示仍在处理中。
     * - 202: 表示已完成但不成功。
     * - 500: 表示发生异常情况。
     */
    @RequestMapping(value = "/certcheck")
    Object createCertCheck(@RequestBody @Validated DomainCertRequest domainCertRequest, HttpServletRequest request, HttpServletRequest response) {
        return domainCertificateService.createCertificateChcekRequest(domainCertRequest.getToken());
    }
}
