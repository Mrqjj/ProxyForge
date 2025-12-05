package com.proxy.forge.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.api.pojo.CheckDeviceInfo;
import com.proxy.forge.api.pojo.FingerprintAnalysisReuslt;
import com.proxy.forge.dto.GlobalSettings;
import com.proxy.forge.dto.ClientLogs;
import com.proxy.forge.dto.GlobalReplace;
import com.proxy.forge.dto.WebSite;
import com.proxy.forge.service.*;
import com.proxy.forge.tools.*;
import com.proxy.forge.vo.fingerprint.ClientFingerprint;
import com.proxy.forge.vo.ResponseApi;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 代理路由实现类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-20 01:59
 **/
@Slf4j
@Service
public class ProxyRouterServiceImpl implements ProxyRouterService {


    @Autowired
    private ResourceLoader resourceLoader;

    // 注入所有实现类。
    @Autowired(required = false)
    private List<CallBackService> callBackServices = new ArrayList<>();
    ;

    @Autowired
    GlobalReplaceService globalReplaceService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    FingerprintAnalysisUtil fingerprintAnalysisUtil;
    @Autowired
    WhiteListService whiteListService;
    @Autowired
    ClientLogsService clientLogsService;
    @Autowired
    private WebSiteService webSiteService;

    /**
     * 检查传入的 HttpServletRequest 和 HttpServletResponse，并执行必要的验证或处理。
     * 该方法可以用于执行预处理步骤，例如身份验证、权限检查或其他形式的请求验证，
     * 以确保请求满足特定条件后才能被进一步处理。
     *
     * @param tk              token
     * @param checkDeviceInfo 终端设备信息
     * @param request         包含客户端数据的 Servlet 请求。
     * @param response        输出的servlet响应用于将处理结果返回客户端。
     * @return 一个对象，封装了对请求处理的结果。具体类型和内容取决于实现逻辑。
     */
    @Override
    public Object check(String tk, CheckDeviceInfo checkDeviceInfo, HttpServletRequest request, HttpServletResponse response) {
        // 客户端ip
        String clientIp = request.getRemoteAddr();
        // 主机名
        String serverName = request.getServerName();
        // 客户浏览器环境信息
        String devInfo = checkDeviceInfo.getEncStr();
        try {
            byte[] originData = Base64Utils.decode(devInfo);
            byte[] key = new byte[32];
            System.arraycopy(originData, originData.length - 48, key, 0, 32);
            byte[] iv = new byte[16];
            System.arraycopy(originData, originData.length - 16, iv, 0, 16);
            byte[] data = new byte[originData.length - 48];
            System.arraycopy(originData, 0, data, 0, originData.length - 48);
            byte[] result = CryptoUtil.aesCbcPkcs7Decrypt(key, iv, data);
            String str = new String(result);
            // 分配数据, 终端唯一标识.
            if (StringUtils.isBlank(tk) || JwtUtils.isExpired(tk)) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "") + System.currentTimeMillis();
                tk = JwtUtils.createToken(DigestUtils.md5Hex(uuid), 1000 * 60 * 60 * 24 * 3, null); // 数据有效期 3天
                Cookie uniqueIdent = new Cookie("tk", tk);
                uniqueIdent.setMaxAge(60 * 60 * 24 * 365);  // cookie 过期时间 一年
                response.addCookie(uniqueIdent);
            }
            // 获取网站配置
            Object webSiteObj = webSiteService.getWebSiteConfig(serverName);
            WebSite webSite;
            if (webSiteObj instanceof WebSite) {
                webSite = (WebSite) webSiteObj;
            } else {
                return webSiteObj;
            }
            // 客户端全局唯一标识。
            String token = JwtUtils.parse(tk).getSubject();
            ClientFingerprint clientFingerprint = JSONObject.parseObject(str, ClientFingerprint.class);
            // TODO:这里防红。
            // 白名单IP 直接放行不做策略检查
            if (!whiteListService.isExistsWhiteList(clientIp)) {
                // 需要准备终端的环境信息， 还有代理信息以及, 该域名的配置信息
                // 读取全局配置
                GlobalSettings globalSettings = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("globalSettings"), GlobalSettings.class);
                FingerprintAnalysisReuslt fingerprintAnalysisReuslt = fingerprintAnalysisUtil.analyze(serverName, clientFingerprint, clientIp, globalSettings);
                if (!fingerprintAnalysisReuslt.isResult()) {
                    log.info("[终端检查 策略不通过] , 拒绝执行. 终端唯一标识: [{}] 终端IP: [{}], 主机名: [{}], 策略: {}", token, clientIp, serverName, fingerprintAnalysisReuslt.getMessage());
                    // 写入日志
                    clientLogsService.saveClientLogs(new ClientLogs(
                            token,
                            "[❌❌❌ 终端检查拒绝]",
                            "/check",
                            "POST",
                            str,
                            "客户端环境检查 不通过, 原因: " + fingerprintAnalysisReuslt.getMessage(),
                            clientIp,
                            serverName,
                            webSite.getId()
                    ));
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseApi(403, "error", "Strategy check failed!"));
                }
            } else {
                log.info("[终端检查] , 终端唯一标识: [{}], 终端IP: [{}], 主机名: [{}], 存在白名单,不拦截", token, clientIp, serverName);
            }

            // 写入日志
            clientLogsService.saveClientLogs(new ClientLogs(
                    token,
                    "[✅✅✅ 终端检查通过]",
                    "/check",
                    "POST",
                    str,
                    "客户端环境检查,IP检查. 通过。白名单ip: [ " + (whiteListService.isExistsWhiteList(clientIp) ? "是" : "否") + " ]",
                    clientIp,
                    serverName,
                    webSite.getId()
            ));
            return ResponseEntity.ok().body(new ResponseApi(200, "success", null));
        } catch (Exception e) {
            log.info("[检查终端环境信息 实现]:  终端IP: [{}], 请求主机名: [{}], 解密终端数据错误.[{}]", clientIp, serverName, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseApi(403, "error", "data error"));
        }
    }

    /**
     * 这里校验客户端的标识， 组装数据。 发送请求。
     *
     * @param tk       客户端唯一标识，用于验证或识别客户端。
     * @param request  包含客户端数据的Servlet请求对象。
     * @param response 用于将处理结果返回给客户端的ServletResponse对象。
     * @return 结果。
     */
    @Override
    public Object startRequest(String tk, HttpServletRequest request, HttpServletResponse response) {
        // 没有获取到有效的终端标识, 回到主流程, 重新开始.
        if (StringUtils.isBlank(tk) || JwtUtils.isExpired(tk)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/").build();
        }
        String token = JwtUtils.parse(tk).getSubject();
        // 获取主机名.
        String serverName = request.getServerName();
        // 客户端IP
        String clientIp = request.getRemoteAddr();
        Object webSiteObj = webSiteService.getWebSiteConfig(serverName);
        WebSite webSiteConfig;
        if (webSiteObj instanceof WebSite) {
            webSiteConfig = (WebSite) webSiteObj;
        } else {
            return webSiteObj;
        }
        // 调用所有接口实现.
        for (CallBackService callBackService : callBackServices) {
            Object result = callBackService.beforeFirstPageRequest(token, serverName, clientIp, webSiteConfig, request, response);
            if (result != null) {
                return result;
            }
        }
        // 这里应该 回调插件 准备请求目标站点第一个页面前的回调。 需要传入 tk 用户终端唯一标识,
        // serverName 当前客户端请求的主机名,clientIp 客户端ip, 全局配置，站点配置
        // 访问目标主页必须使用 /index  可以在 beforeFirstPageRequest 内修改响应，
        // return ResponseEntity.status(HttpStatus.FOUND)
        //                .header("Location", "/xx/xxxxxxxxx/xxxxxxxxxxxxxxxxxxxxxxxx")
        //                .build();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/index")
                .build();
    }

    /**
     * 处理接收的 HttpServletRequest 和 HttpServletResponse，并发送
     * 将其交给适当的处理者或服务机构。该方法旨在处理
     * 请求转发、处理和响应生成，在代理或网关中进行
     * 服务架构。
     *
     * @param tk       客户端唯一标识。
     * @param request  包含客户端数据的 Servlet 请求。
     * @param response 输出的servlet响应用于将处理结果返回客户端。
     * @return 一个ResponseEntity对象，封装了响应体和状态码，头部，以及 HTTP 响应所需的其他元数据。
     *
     */
    @Override
    public Object dispatch(String tk, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //先查询是否有路径匹配的替换
        GlobalReplace globalReplace = globalReplaceService.getGlobalReplace(request.getRequestURI());
        if (globalReplace != null) {
            return ResponseEntity.ok().contentType(MediaType.valueOf(globalReplace.getContentType()))
                    .body(globalReplace.getResponseContent());
        }
        //读取全局配置
        GlobalSettings globalSettings = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("globalSettings"), GlobalSettings.class);
        //请求路径
        String path = request.getRequestURI();
        Resource resource;
        if (path.equalsIgnoreCase("/")) {
            resource = resourceLoader.getResource("classpath:/static/index.html");
        } else {
            resource = resourceLoader.getResource("classpath:/static" + path);
        }
        // 如果访问的文件 本地存在，则返回本地内容
        if (resource.exists()) {
            String fileName = resource.getFile().getName();
            MediaType mediaType = switch (fileName.substring(fileName.lastIndexOf(".") + 1)) {
                case "html" -> MediaType.TEXT_HTML;
                case "css" -> MediaType.valueOf("text/css");
                case "js" -> MediaType.valueOf("application/javascript");
                case "json" -> MediaType.APPLICATION_JSON;
                case "png" -> MediaType.IMAGE_PNG;
                case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
                case "gif" -> MediaType.IMAGE_GIF;
                case "svg" -> MediaType.valueOf("image/svg+xml");
                default -> MediaType.APPLICATION_OCTET_STREAM;
            };
            byte[] bytes = resource.getInputStream().readAllBytes();
            if (fileName.equalsIgnoreCase("index.html")) {
                String fileText = new String(bytes, StandardCharsets.UTF_8);
                fileText = fileText.replaceAll("\\$\\{\\{errorUrl}}", globalSettings.getDefaultUrl());
                fileText = fileText.replaceAll("<!--统计代码-->", globalSettings.getAnalyticsCode());
                bytes = fileText.getBytes(StandardCharsets.UTF_8);
            }
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(bytes);
        }

        // 没有获取到有效的终端标识, 回到主流程, 重新开始.
        if (StringUtils.isBlank(tk) || JwtUtils.isExpired(tk)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/").build();
        }

        // 查询字符串
        String queryString = request.getQueryString();
        if (path.equalsIgnoreCase("/index") && StringUtils.isBlank(queryString)) {
            path = "/";
        }
        // 获取主机名.
        String serverName = request.getServerName();
        // 客户端IP
        String clientIp = request.getRemoteAddr();
        // 客户端唯一标识
        String token = JwtUtils.parse(tk).getSubject();
        // 获取网站配置
        Object webSiteObj = webSiteService.getWebSiteConfig(serverName);
        WebSite webSiteConfig;
        if (webSiteObj instanceof WebSite) {
            webSiteConfig = (WebSite) webSiteObj;
        } else {
            return webSiteObj;
        }
        // 组装完整的请求地址.
        String url;
        if (StringUtils.isNotBlank(queryString)) {
            url = webSiteConfig.getTargetUrl() + path + "?" + queryString;
        } else {
            url = webSiteConfig.getTargetUrl() + path;
        }

        // 写入日志
        clientLogsService.saveClientLogs(new ClientLogs(
                JwtUtils.parse(tk).getSubject(),
                "[📡📡📡 请求目标站]",
                url,
                request.getMethod(),
                request.getMethod().equalsIgnoreCase("POST") ? StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8) : "",
                "发送请求到目标地址: " + url,
                clientIp,
                serverName,
                webSiteConfig.getId()
        ));


        HashMap<String, Object> header = generateHader(request);
        byte[] res;
        StringBuilder proxyStr = new StringBuilder(); // 初始为空
        if (request.getMethod().equalsIgnoreCase("POST")) {
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            // 调用所有接口实现.
            for (CallBackService callBackService : callBackServices) {
                Object result = callBackService.requestBefore(token, serverName,
                        clientIp, webSiteConfig, request, response, body,
                        header, url, proxyStr);
                if (result != null) {
                    return result;
                }
            }
            res = HttpUtils.sendPostRequest(url, body, header, proxyStr.isEmpty() ? null : proxyStr.toString());
        } else if (request.getMethod().equalsIgnoreCase("GET")) {
            // 调用所有接口实现.
            for (CallBackService callBackService : callBackServices) {
                Object result = callBackService.requestBefore(token, serverName,
                        clientIp, webSiteConfig, request, response, null,
                        header, url, proxyStr);
                if (result != null) {
                    return result;
                }
            }
            res = HttpUtils.sendGetRequest(url, header, proxyStr.isEmpty() ? null : proxyStr.toString());
        } else {
            res = new byte[0];
        }
        HttpResponse httpResponse = (HttpResponse) header.get("response");
        generateResponHeader(httpResponse, response);
        // 调用所有接口实现.
        for (CallBackService callBackService : callBackServices) {
            Object result = callBackService.requestAfter(token, serverName,
                    clientIp, webSiteConfig, request, response,
                    httpResponse, url, res);
            if (result != null) {
                return result;
            }
        }
        return ResponseEntity.status(HttpStatus.valueOf(httpResponse.getStatusLine().getStatusCode()))
                .body(res);
    }

    /**
     * 处理接收的 HttpServletRequest 和 HttpServletResponse，并将流式数据
     * 交给适当的处理者或服务机构。此方法专为需要处理大文件或持续数据流的场景设计，
     * 在代理或网关的服务架构中进行请求转发、处理和响应生成。
     *
     * @param request  包含客户端数据的 Servlet 请求。
     * @param response 输出的servlet响应用于将处理结果返回客户端。
     * @return 一个对象，封装了对请求处理的结果，具体类型取决于实现逻辑。
     * @throws Exception 如果在处理过程中发生错误，则抛出异常。
     */
    @Override
    public Object dispatchStream(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String, Object> header = generateHader(request);
        InputStream in = HttpUtils.sendGetStreamRequest("https://vjs.zencdn.net/v/oceans.mp4", header, null);
        HttpResponse httpResponse = (HttpResponse) header.get("response");
        generateResponHeader(httpResponse, response);
        ServletOutputStream out = response.getOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            out.flush();  // 可选，加快实时输出
        }
        in.close();
        out.close();
        return null;
    }

    /**
     * 从给定的HttpServletRequest中生成一个包含请求头信息的HashMap。
     *
     * @param request 用于提取头信息的HttpServletRequest对象。
     * @return 包含请求头信息的HashMap，其中键是头名称，值是对应的头值。如果某个头不存在，则其值为""。
     */
    public HashMap<String, Object> generateHader(HttpServletRequest request) {
        HashMap<String, Object> headMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            if (key.equalsIgnoreCase("host") || key.equalsIgnoreCase("content-length")) {
                continue;
            }
            headMap.put(key, request.getHeader(key) == null ? "" : request.getHeader(key));
        }
        return headMap;
    }

    /**
     * 从给定的HttpResponse对象中提取所有响应头，并将它们设置到HttpServletResponse对象中。
     * 同时，也将状态码从HttpResponse复制到HttpServletResponse。
     *
     * @param httpResponse 从中提取响应头和状态码的HttpResponse对象。
     * @param response     将响应头和状态码设置到此HttpServletResponse对象。
     */
    public void generateResponHeader(HttpResponse httpResponse, HttpServletResponse response) {
        if (httpResponse == null) {
            return;
        }
        for (Header h : httpResponse.getAllHeaders()) {
            response.setHeader(h.getName(), h.getValue());
        }
        response.setStatus(httpResponse.getStatusLine().getStatusCode());
    }
}
