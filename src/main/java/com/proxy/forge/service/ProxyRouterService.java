package com.proxy.forge.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: ForwordRequestService</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-20 00:57
 **/
public interface ProxyRouterService {

    /**
     * 处理接收的 HttpServletRequest 和 HttpServletResponse，并发送
     * 将其交给适当的处理者或服务机构。该方法旨在处理
     * 请求转发、处理和响应生成，在代理或网关中进行
     * 服务架构。
     *
     * @param request 包含客户端数据的 Servlet 请求。
     * @param response 输出的servlet响应用于将处理结果返回客户端。
     * @return 一个ResponseEntity对象，封装了响应体和状态码，头部，以及 HTTP 响应所需的其他元数据。
     *
     */
    ResponseEntity<?> dispatch(HttpServletRequest request, HttpServletResponse response);

}
