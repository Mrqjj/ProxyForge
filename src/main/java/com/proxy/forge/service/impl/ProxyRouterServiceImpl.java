package com.proxy.forge.service.impl;

import com.proxy.forge.service.ProxyRouterService;
import com.proxy.forge.tools.HttpUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.io.InputStream;
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
@Service
public class ProxyRouterServiceImpl implements ProxyRouterService {


    @Override
    public Object dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            if (key.equalsIgnoreCase("host")) {
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
     * @param response 将响应头和状态码设置到此HttpServletResponse对象。
     */
    public void generateResponHeader(HttpResponse httpResponse, HttpServletResponse response) {
        for (Header h : httpResponse.getAllHeaders()) {
            response.setHeader(h.getName(), h.getValue());
        }
        response.setStatus(httpResponse.getStatusLine().getStatusCode());
    }
}
