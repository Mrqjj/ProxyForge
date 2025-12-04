package com.proxy.forge.hao123.service;

import com.proxy.forge.dto.ClientLogs;
import com.proxy.forge.dto.WebSite;
import com.proxy.forge.service.CallBackService;
import com.proxy.forge.service.ClientLogsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.hao123.service</p>
 * <p>Description: 实现回调接口</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-04 20:40
 **/
@Service
public class Hao123CallBackServiceImpl implements CallBackService {

    @Autowired
    ClientLogsService clientLogsService;

    /**
     *
     * @param token      与请求关联的认证或会话令牌。终端请求唯一标识.
     * @param serverName 处理请求的服务器名称。
     * @param clientIp   请求客户端的IP地址。
     * @param webSite    包含被访问网站详细信息的对象，如域名和访问策略。
     * @param request    表示 HTTP 请求的 HttpServletRequest 对象。
     * @param response   HttpServletResponse 对象用于将响应返回客户端。
     * @return
     */
    @Override
    public Object beforeFirstPageRequest(String token, String serverName, String clientIp, WebSite webSite, HttpServletRequest request, HttpServletResponse response) {
        if (webSite.getTargetUrl().contains("hao123.com")) {
            System.out.println("这里是发送请求到目标站点第一个页面前的回调,可以修改请求路径和参数");
            // 记录日志
            clientLogsService.saveClientLogs(new ClientLogs(
                    token,
                    "[💨💨💨 请求主页前回调]",
                    "请求路径",
                    request.getMethod(),
                    "请求体信息",
                    "这里是发送请求到目标站点第一个页面前的回调,可以修改请求路径和参数.",
                    clientIp,
                    serverName,
                    webSite.getId()
            ));
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/error/error404/error-high.html")
                    .build();
        }
        return null;
    }

    /**
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
     * @return
     */
    @Override
    public Object requestBefore(String token, String serverName, String clientIp, WebSite webSite, HttpServletRequest request, HttpServletResponse response, String body, HashMap<String, Object> header, String targetUrl, StringBuilder proxyStr) {
        if (webSite.getTargetUrl().contains("hao123.com")) {
            System.out.println("发送请求之前回调这里");
            //修改代理 支持 http socks5  支持账密模式  (http|socks5)://userName:passWord@ip:port
            proxyStr.append("socks5://127.0.0.1:7890");
            // 记录日志
            clientLogsService.saveClientLogs(new ClientLogs(
                    token,
                    "[💨💨💨 发送请求前回调]",
                    request.getRequestURI(),
                    request.getMethod(),
                    body,
                    "发送请求之前回调这里, 且替换了代理信息, 请求目标地址为: " + targetUrl,
                    clientIp,
                    serverName,
                    webSite.getId()
            ));
        }
        return null;
    }

    /**
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
     * @return
     */
    @Override
    public Object requestAfter(String token, String serverName, String clientIp, WebSite webSite, HttpServletRequest request, HttpServletResponse response, HttpResponse httpResponse, String targetUrl, byte[] resBody) {
        if (webSite.getTargetUrl().contains("hao123.com")) {
            // 这里是发送请求后的回调
            String bodyStr = new String(resBody);
            clientLogsService.saveClientLogs(new ClientLogs(
                    token,
                    "[💨💨💨 发送请求之后回调]",
                    request.getRequestURI(),
                    request.getMethod(),
                    bodyStr.length() > 1000 ? bodyStr.substring(0, 1000) : bodyStr,
                    "这里是发送请求后的回调, 且替换了代理信息,响应状态码：" + httpResponse.getStatusLine().getStatusCode(),
                    clientIp,
                    serverName,
                    webSite.getId()
            ));
            if(httpResponse.getStatusLine().getStatusCode()==302 || httpResponse.getStatusLine().getStatusCode()==301){
                if(response.containsHeader("Location") && response.getHeader("Location").startsWith("http")){
                    try {
                        URL u = new URL(response.getHeader("Location"));
                        String redirect = u.getPath()+"?"+u.getQuery();
                        response.setHeader("Location", redirect);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }
}
