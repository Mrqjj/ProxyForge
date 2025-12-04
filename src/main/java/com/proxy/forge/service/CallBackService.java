package com.proxy.forge.service;

import com.proxy.forge.dto.WebSite;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;

import java.util.HashMap;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 回调接口</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-04 19:49
 **/
public interface CallBackService {

    // 请求目标站之前回调.

    /**
     * 在向目标网站发出首页请求前调用回调方法。可以修改请求的目标页面地址
     *
     * @param token      与请求关联的认证或会话令牌。终端请求唯一标识.
     * @param serverName 处理请求的服务器名称。
     * @param clientIp   请求客户端的IP地址。
     * @param webSite    包含被访问网站详细信息的对象，如域名和访问策略。
     * @param request    表示 HTTP 请求的 HttpServletRequest 对象。
     * @param response   HttpServletResponse 对象用于将响应返回客户端。
     * @return 可用于修改请求或响应行为的对象。该对象的具体类型和用途取决于实现方式。 返回null  不影响后续流程， 只是修改部分参数，
     */
    Object beforeFirstPageRequest(String token, String serverName, String clientIp, WebSite webSite, HttpServletRequest request, HttpServletResponse response);

    //发送请求之前回调

    /**
     * 在向目标网站发出请求之前调用的回调方法。此方法允许在实际请求发送前进行一些预处理，例如修改请求参数、设置响应头或执行安全检查等。
     *
     * @param token      与请求关联的认证或会话令牌。终端请求唯一标识。
     * @param serverName 处理请求的服务器名称。
     * @param clientIp   请求客户端的IP地址。
     * @param webSite    包含被访问网站详细信息的对象，如域名和访问策略。
     * @param request    表示 HTTP 请求的 HttpServletRequest 对象。
     * @param response   HttpServletResponse 对象用于将响应返回客户端。
     * @param body       请求体数据。  修改生效
     * @param header     请求头数据。  修改生效
     * @param targetUrl  请求目标地址。 修改生效
     * @param proxyStr   分配代理字符串。 修改生效 不设置 将不使用代理
     * @return 可用于修改请求或响应行为的对象。该对象的具体类型和用途取决于实现方式。返回null  不影响后续流程， 只是修改部分参数，
     */
    Object requestBefore(String token, String serverName, String clientIp, WebSite webSite, HttpServletRequest request, HttpServletResponse response, String body, HashMap<String, Object> header, String targetUrl, StringBuilder proxyStr);

    //发送请求之后回调

    /**
     * 在向目标网站发出请求之后调用的回调方法。此方法允许在请求处理完成后进行一些后处理，例如记录日志、修改响应或执行清理操作等。
     *
     * @param token        与请求关联的认证或会话令牌。终端请求唯一标识。
     * @param serverName   处理请求的服务器名称。
     * @param clientIp     请求客户端的IP地址。
     * @param webSite      包含被访问网站详细信息的对象，如域名和访问策略。
     * @param request      表示 HTTP 请求的 HttpServletRequest 对象。
     * @param response     HttpServletResponse 对象用于将响应返回客户端。
     * @param httpResponse HttpResponse 对象 Apache httpClient
     * @param targetUrl    请求的目标地址
     * @param resBody      请求响应体,字节类型。 如果服务器端返回的数据带有压缩头，需要先处理解压后才能得到明文
     * @return 可用于修改请求或响应行为的对象。该对象的具体类型和用途取决于实现方式。  返回null  不影响后续流程， 只是修改部分参数，
     */
    Object requestAfter(String token, String serverName, String clientIp, WebSite webSite, HttpServletRequest request, HttpServletResponse response, HttpResponse httpResponse, String targetUrl, byte[] resBody);
}
