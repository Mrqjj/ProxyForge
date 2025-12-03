package com.proxy.forge.controller;

import com.proxy.forge.api.pojo.*;
import com.proxy.forge.service.*;
import com.proxy.forge.service.impl.ApiKeysServiceImpl;
import com.proxy.forge.tools.CertificateManagement;
import com.proxy.forge.tools.DNSUtils;
import com.proxy.forge.tools.GlobalStaticVariable;
import com.proxy.forge.vo.ResponseApi;
import com.proxy.forge.vo.WhiteListVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.controller</p>
 * <p>Description: 后台管理控制器</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
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
    @Autowired
    ApiKeysService apiKeysService;
    @Autowired
    WhiteListService whiteListService;
    @Autowired
    ClientLogsService clientLogsService;

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

    /**
     * 删除指定ID的网站。
     *
     * @param query    包含待删除网站ID的对象
     * @param request  HttpServletRequest 对象，可用于获取客户端请求的信息。
     * @param response HttpServletResponse 对象，可用于向客户端发送响应。
     * @return 返回一个对象，表示删除操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    @RequestMapping(value = "/delWebSite")
    Object deleteWebSite(@RequestBody @Validated QueryById query, HttpServletRequest request, HttpServletResponse response) {
        return websiteService.deleteWebSite(query);
    }

    /**
     * 根据提供的类型和域处理DNS查询。
     *
     * @param dnsQuery 包含查询域和类型的 DNS 查询对象。
     * @param request  HttpServletRequest 对象处理收到的 HTTP 请求。
     * @param response HttpServletResponse 对象处理输出的 HTTP 响应。
     * @return 包含状态码、消息和DNS查询结果的ResponseApi对象。
     */
    @RequestMapping(value = "/dnsQuery")
    Object dnsQuery(@RequestBody @Validated DNSQuery dnsQuery, HttpServletRequest request, HttpServletResponse response) {
        List<DNSUtils.ResultItem> result = switch (dnsQuery.getType()) {
            case "A" -> DNSUtils.getA(dnsQuery.getDomain());
            case "AAAA" -> DNSUtils.getAAAA(dnsQuery.getDomain());
            case "CNAME" -> DNSUtils.getCNAME(dnsQuery.getDomain());
            case "TXT" -> DNSUtils.getTXT(dnsQuery.getDomain());
            case "MX" -> DNSUtils.getMX(dnsQuery.getDomain());
            case "NS" -> DNSUtils.getNS(dnsQuery.getDomain());
            case "SOA" -> DNSUtils.getSOA(dnsQuery.getDomain());
            case "SRV" -> DNSUtils.getSRV(dnsQuery.getDomain());
            case "NAPTR" -> DNSUtils.getNAPTR(dnsQuery.getDomain());
            case "CAA" -> DNSUtils.getCAA(dnsQuery.getDomain());
            case "PTR" -> DNSUtils.getPTR(dnsQuery.getDomain());
            default -> null;
        };
        if (result == null) {
            return new ResponseApi(201, GlobalStaticVariable.API_MESSAGE_FAIL, null);
        }
        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, result);
    }

    /**
     * 根据提供的搜索条件处理 API 密钥列表请求。
     *
     * @param searchApiKey 用于筛选 API 密钥的搜索条件
     * @param request      包含客户端请求的 HttpServletRequest 对象
     * @param response     HttpServletResponse 对象，用于将响应返回客户端
     * @return 表示符合给定搜索条件的API密钥列表的对象
     */
    @RequestMapping(value = "/apiKeyList")
    Object apiKeyList(@RequestBody @Validated SearchApiKey searchApiKey, HttpServletRequest request, HttpServletResponse response) {
        return apiKeysService.apiKeyList(searchApiKey);
    }

    /**
     * 处理保存API密钥的请求。
     *
     * @param saveApiKey 包含待保存 API 密钥详细信息的对象
     * @param request    代表客户端请求的 HttpServletRequest 对象
     * @param response   HttpServletResponse 对象，用于将响应返回客户端
     * @return 保存作的结果，根据结果，可能是成功消息或错误
     */
    @RequestMapping(value = "/saveApiKey")
    Object saveApiKey(@RequestBody @Validated SaveApiKey saveApiKey, HttpServletRequest request, HttpServletResponse response) {
        return apiKeysService.saveApiKey(saveApiKey);
    }

    /**
     * 根据所提供查询删除API密钥。
     *
     * @param query    API 密钥 ID 的 QueryById 对象，以删除
     * @param request  代表当前HTTP请求的HttpServletRequest对象
     * @param response HttpServletResponse 对象，用于向客户端发送响应
     * @return 对象，通常是响应实体或状态，表示作结果
     */
    @RequestMapping(value = "/deleteApiKey")
    Object deleteApiKey(@RequestBody @Validated QueryById query, HttpServletRequest request, HttpServletResponse response) {
        return apiKeysService.deleteApiKey(query);
    }

    /**
     * 处理来电请求的IP白名单流程。
     *
     * @param request  包含客户端对服务组请求的 HttpServletRequest 对象。
     * @param response 包含 servlet 发送给客户端的响应的 HttpServletResponse 对象。
     * @return 表示IP白名单作结果或状态的对象。
     */
    @RequestMapping(value = "/whiteList")
    Object ipWhiteList(HttpServletRequest request, HttpServletResponse response) {
        List<WhiteListVO> list = whiteListService.list();
        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, list, list.size());
    }

    /**
     * 将新的IP地址保存到白名单。
     *
     * @param saveIpWhiteList 包含 IP、到期时间和待保存笔记的对象。
     * @param request         当前请求的 HttpServletRequest 对象。
     * @param response        当前响应的 HttpServletResponse 对象。
     * @return 一个状态码为200的ResponseApi对象，作成功时发送成功消息。
     */
    @RequestMapping(value = "/saveWhiteList")
    Object saveWhiteList(@RequestBody @Validated SaveIpWhiteList saveIpWhiteList, HttpServletRequest request, HttpServletResponse response) {
        whiteListService.addIp(saveIpWhiteList.getIp(), saveIpWhiteList.getTtl(), saveIpWhiteList.getNotes());
        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, null);
    }

    /**
     * 从白名单中删除一个IP。
     *
     * @param saveIpWhiteList 包含待删除IP的对象。该对象必须经过验证后才能传递给该方法。
     * @param request         HttpServletRequest 对象，代表客户端的请求。
     * @param response        HttpServletResponse 对象用于向客户端发送响应。
     * @return 表示作结果的对象，可以是消息、状态或其他相关信息。
     */
    @RequestMapping(value = "/deleteWhiteList")
    Object deleteWhiteList(@RequestBody @Validated SaveIpWhiteList saveIpWhiteList, HttpServletRequest request, HttpServletResponse response) {
        whiteListService.removeIp(saveIpWhiteList.getIp());
        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, null);
    }

    /**
     * 根据请求记录地理国家信息。
     *
     * @param geoCountry 请求参数。
     * @param request    包含客户端请求信息的 HttpServletRequest 对象。
     * @param response   HttpServletResponse 对象用于向客户端发送响应。
     * @return 表示作结果的对象，可以是状态或消息。
     */
    @RequestMapping(value = "/geoCountry")
    Object logGeoCountry(@RequestBody @Validated GeoCountry geoCountry, HttpServletRequest request, HttpServletResponse response) {
        return clientLogsService.logGeoCountry(geoCountry);
    }
    @RequestMapping(value = "/geoCity")
    Object logGeoCity(@RequestBody @Validated GeoCountry geoCountry, HttpServletRequest request, HttpServletResponse response) {
        return clientLogsService.logGeoCity(geoCountry);
    }

    /**
     * 根据提供的地理国家信息检索指定域名的统计数据。
     *
     * @param geoCountry 地理国家信息，必须验证。该参数用于过滤领域统计量。
     * @param request 包含客户端请求的 HttpServletRequest 对象。
     * @param response 用于将响应返回客户端的 HttpServletResponse 对象。
     * @return 包含与域名相关的统计数据的对象，按所给地理国家进行筛选。该对象的具体结构取决于实现方式
     * 以及领域统计的具体要求。
     */
    @RequestMapping(value = "/domainStats")
    Object domainStats(@RequestBody @Validated GeoCountry geoCountry, HttpServletRequest request, HttpServletResponse response) {
        return clientLogsService.domainStats(geoCountry);
    }

/**
     * 检索所提供地理国家的趋势统计数据。
     *
     * @param geoCountry 用于获取趋势统计的地理国家。该参数应为有效的GeoCountry对象。
     * @param request 包含客户端对服务组请求的 HttpServletRequest 对象。
     * @param response HttpServletResponse 对象，将响应返回客户端。
     * @return 包含指定地理国家趋势统计的对象。该对象的具体结构和内容取决于clientLogsService.trendStats方法的实现
     * .
     */
    @RequestMapping(value = "/trendStats")
    Object trendStats(@RequestBody @Validated GeoCountry geoCountry, HttpServletRequest request, HttpServletResponse response) {
        return clientLogsService.trendStats(geoCountry);
    }



}
