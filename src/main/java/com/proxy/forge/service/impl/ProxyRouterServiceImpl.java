package com.proxy.forge.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.api.pojo.CheckDeviceInfo;
import com.proxy.forge.api.pojo.GlobalSettings;
import com.proxy.forge.dto.GlobalReplace;
import com.proxy.forge.service.GlobalReplaceService;
import com.proxy.forge.service.ProxyRouterService;
import com.proxy.forge.tools.Base64Utils;
import com.proxy.forge.tools.CryptoUtil;
import com.proxy.forge.tools.HttpUtils;
import com.proxy.forge.vo.ResponseApi;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Enumeration;
import java.util.HashMap;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 代理路由实现类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
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

    @Autowired
    GlobalReplaceService globalReplaceService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 处理接收的 HttpServletRequest 和 HttpServletResponse，并发送
     * 将其交给适当的处理者或服务机构。该方法旨在处理
     * 请求转发、处理和响应生成，在代理或网关中进行
     * 服务架构。
     *
     * @param request  包含客户端数据的 Servlet 请求。
     * @param response 输出的servlet响应用于将处理结果返回客户端。
     * @return 一个ResponseEntity对象，封装了响应体和状态码，头部，以及 HTTP 响应所需的其他元数据。
     *
     */
    @Override
    public Object dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //先查询是否有路径匹配的替换
        GlobalReplace globalReplace = globalReplaceService.getGlobalReplace(request.getRequestURI());
        if (globalReplace != null) {
            return ResponseEntity.ok().contentType(MediaType.valueOf(globalReplace.getContentType()))
                    .body(globalReplace.getResponseContent());
        }

        //读取全局配置
        GlobalSettings globalSettings = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("globalSettings"), GlobalSettings.class);



        // 查找数据库是否有匹配的 代理网站列表

        String path = request.getRequestURI();
        Resource resource = resourceLoader.getResource("classpath:/static" + (path.equals("/") ? "/index.html" : path));
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

        // 客户端IP
        String clientIp = request.getRemoteAddr();
        // 获取主机名.
        String serverName = request.getServerName();

        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        HashMap<String, Object> header = generateHader(request);
        if (request.getMethod().equalsIgnoreCase("POST")) {
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } else if (request.getMethod().equalsIgnoreCase("GET")) {

        }
        HttpResponse httpResponse = (HttpResponse) header.get("response");
        generateResponHeader(httpResponse, response);
        return null;
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

    /**
     * 检查传入的 HttpServletRequest 和 HttpServletResponse，并执行必要的验证或处理。
     * 该方法可以用于执行预处理步骤，例如身份验证、权限检查或其他形式的请求验证，
     * 以确保请求满足特定条件后才能被进一步处理。
     *
     * @param checkDeviceInfo 终端设备信息
     * @param request         包含客户端数据的 Servlet 请求。
     * @param response        输出的servlet响应用于将处理结果返回客户端。
     * @return 一个对象，封装了对请求处理的结果。具体类型和内容取决于实现逻辑。
     */
    @Override
    public Object check(CheckDeviceInfo checkDeviceInfo, HttpServletRequest request, HttpServletResponse response) {
        // 获取环境信息
        String devInfo = checkDeviceInfo.getEncStr();
        byte[] originData = Base64Utils.decode(devInfo);
        String remoteIP = request.getRemoteAddr();
        String serverHost = request.getServerName();

        byte[] key = new byte[32];
        System.arraycopy(originData, originData.length - 48, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(originData, originData.length - 16, iv, 0, 16);
        byte[] data = new byte[originData.length - 48];
        System.arraycopy(originData, 0, data, 0, originData.length - 48);
        try {
            byte[] result = CryptoUtil.aesCbcPkcs7Decrypt(key, iv, data);
            String str = new String(result);
            JSONObject clientEnvInfo = JSONObject.parse(str);
            // TODO:这里防红 待处理。
        } catch (Exception e) {
            log.info("[检查终端环境信息 实现]:  终端IP: [{}], 请求主机名: [{}], 解密终端数据错误.[{}]", remoteIP, serverHost, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseApi(403, "error", "data error"));
        }
        return null;
    }
}
