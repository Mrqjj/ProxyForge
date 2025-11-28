package com.proxy.forge.controller;

import com.proxy.forge.api.pojo.*;
import com.proxy.forge.service.*;
import com.proxy.forge.tools.CertificateManagement;
import com.proxy.forge.vo.ResponseApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    @Autowired
    DomainService domainService;
    @Autowired
    UserSerivce userSerivce;
    @Autowired
    GlobalSettingService globalSettingService;
    @Autowired
    GlobalReplaceService globalInterceptorService;
    @Autowired
    GlobalReplaceService globalReplaceService;
    @Autowired
    WebSiteService websiteService;


    @RequestMapping(value = "/login")
    Object login(@RequestBody @Validated UserLogin userLogin, HttpServletRequest request, HttpServletResponse response) {
        return userSerivce.login(userLogin, request, response);
    }

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

    /**
     * 保存或更新域名信息。
     *
     * @param saveDomain 待保存的Domain对象，包含域名、状态等信息。
     * @param request    服务器接收到的HTTP请求对象。
     * @param response   服务器发送回客户端的HTTP响应对象。
     * @return 返回一个表示操作结果的对象。如果返回值大于0，表示至少有一行被成功保存或更新；如果返回值为0，则表示没有找到匹配的记录或保存/更新操作未改变任何数据。
     * @throws Exception 如果在保存过程中发生异常，则抛出此异常。
     */
    @RequestMapping(value = "/savedomain")
    Object saveDomain(@RequestBody @Validated SaveDomain saveDomain, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return domainService.saveDomain(saveDomain);
    }

    /**
     * 获取域名列表。
     *
     * @param searchDomain 参数对象。
     * @param requestr     服务器接收到的HTTP请求对象。
     * @param response     服务器发送回客户端的HTTP响应对象。
     * @return 返回表示域名列表或相关状态的对象。
     */
    @RequestMapping(value = "/domainlist")
    Object domainList(@RequestBody @Validated SearchDomain searchDomain, HttpServletRequest requestr, HttpServletResponse response) {
        return domainService.domainList(searchDomain);
    }

    /**
     * 获取所有域名信息。
     *
     * @param request  服务器接收到的HTTP请求对象。
     * @param response 服务器发送回客户端的HTTP响应对象。
     * @return 返回表示所有域名信息或相关状态的对象。
     */
    @RequestMapping(value = "/domainAll")
    Object domainAll(HttpServletRequest request, HttpServletResponse response) {
        return domainService.domainAll();
    }


    /**
     * 获取全局设置。
     *
     * @param request  服务器接收到的HTTP请求对象。
     * @param response 服务器发送回客户端的HTTP响应对象。
     * @return 返回表示全局设置的对象。
     */
    @RequestMapping(value = "/getSettings")
    Object getGlobalSettings(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseApi(200, "获取成功", globalSettingService.getGlobalSetting());
    }

    /**
     * 保存或更新全局设置。
     *
     * @param globalSettings 包含要保存的全局设置信息的对象。
     * @param request        服务器接收到的HTTP请求对象。
     * @param response       服务器发送回客户端的HTTP响应对象。
     * @return 返回一个表示操作结果的对象。
     */
    @RequestMapping(value = "/saveSettings")
    Object saveGlobalSettings(@RequestBody @Validated GlobalSettings globalSettings, HttpServletRequest request, HttpServletResponse response) {
        return new ResponseApi(200, "操作成功", globalSettingService.saveGlobalSettings(globalSettings));
    }

    /**
     * 获取全局替换配置。
     *
     * @param request  服务器接收到的HTTP请求对象。
     * @param response 服务器发送回客户端的HTTP响应对象。
     * @return 返回表示全局替换匹配内容数据列表。
     */
    @RequestMapping(value = "/globalReplace")
    Object getGlobalReplace(HttpServletRequest request, HttpServletResponse response) {
        return globalInterceptorService.getGlobalReplace(request, response);
    }

    /**
     * 保存或更新全局替换配置。
     *
     * @param saveGlobalReplace 包含要保存的全局替换配置信息的对象。
     * @param request           服务器接收到的HTTP请求对象。
     * @param response          服务器发送回客户端的HTTP响应对象。
     * @return 返回一个表示操作结果的对象。
     */
    @RequestMapping(value = "/saveGlobalReplace")
    Object saveGlobalReplace(@RequestBody @Validated SaveGlobalReplace saveGlobalReplace, HttpServletRequest request, HttpServletResponse response) {
        return globalInterceptorService.saveGlobalReplace(saveGlobalReplace, request, response);
    }

    /**
     * 删除指定的全局替换配置。
     *
     * @param deleteById 包含要删除的全局替换配置ID的信息对象。
     * @param request    服务器接收到的HTTP请求对象。
     * @param response   服务器发送回客户端的HTTP响应对象。
     * @return 返回一个表示操作结果的对象。
     */
    @RequestMapping(value = "/delGlobalReplace")
    Object deleteGlobalReplace(@RequestBody @Validated QueryById deleteById, HttpServletRequest request, HttpServletResponse response) {
        return globalReplaceService.deleteGlobalReplace(deleteById);
    }

    /**
     * 获取可用的域名列表。
     *
     * @param request  服务器接收到的HTTP请求对象。
     * @param response 服务器发送回客户端的HTTP响应对象。
     * @return 返回表示可用域名列表或相关状态的对象。
     */
    @RequestMapping(value = "/availableDomains")
    Object availableDomains(HttpServletRequest request, HttpServletResponse response) {
        return domainService.availableDomains();
    }


    /**
     * 保存或更新网站信息。
     *
     * @param saveWebSite 包含要保存的网站信息的对象。
     * @param request     服务器接收到的HTTP请求对象。
     * @param response    服务器发送回客户端的HTTP响应对象。
     * @return 返回一个表示操作结果的对象。
     */
    @RequestMapping(value = "/saveSite")
    Object saveWebSite(@RequestBody @Validated SaveWebSite saveWebSite, HttpServletRequest request, HttpServletResponse response) {
        return websiteService.save(saveWebSite);
    }

    /**
     * 获取站点列表。
     *
     * @param searchWebSite 用于搜索站点的参数，包括关键词、域名筛选条件、状态筛选条件、页码及每页数量等信息
     * @param request       包含客户端数据的 Servlet 请求
     * @param response      输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示查询到的站点列表。具体返回对象的类型和结构取决于实现逻辑
     */
    @RequestMapping(value = "/webSiteList")
    Object webSiteList(@RequestBody @Validated SearchWebSite searchWebSite, HttpServletRequest request, HttpServletResponse response) {
        return websiteService.webSiteList(searchWebSite, request, response);
    }

    /**
     * 检索特定网站的详细信息。
     *
     * @param query    包含网站ID的查询对象，用于获取详细信息。该对象应为有效对象而非空对象。
     * @param request  HttpServletRequest 对象，可用于获取客户端请求的信息。
     * @param response HttpServletResponse 对象，可用于向客户端发送响应。
     * @return 代表网站详细信息的对象。该对象的具体类型和结构取决于“websiteService.webSiteDetail”的实现方式方法。
     *
     */
    @RequestMapping(value = "/webSiteDetail")
    Object webSiteDetail(@RequestBody @Validated QueryById query, HttpServletRequest request, HttpServletResponse response) {
        return websiteService.webSiteDetail(query);
    }


    @RequestMapping(value = "delWebSite")
    Object deleteWebSite(@RequestBody @Validated QueryById query, HttpServletRequest request, HttpServletResponse response) {
        return websiteService.deleteWebSite(query);
    }
}
