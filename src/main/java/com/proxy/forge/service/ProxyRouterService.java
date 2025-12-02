package com.proxy.forge.service;

import com.proxy.forge.api.pojo.CheckDeviceInfo;
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
     * 检查接收的 HttpServletRequest 和 HttpServletResponse，并执行必要的验证或处理。
     * 该方法可能用于在进一步处理请求之前进行预检查，确保请求的有效性或安全性。检查body体数据
     *
     * @param tk              客户端cookie。
     * @param checkDeviceInfo 终端设备信息。
     * @param request         包含客户端数据的 Servlet 请求。
     * @param response        输出的servlet响应用于将处理结果返回客户端。
     * @return 一个对象，表示检查的结果。具体类型和内容取决于实现逻辑。
     * @throws Exception 如果在处理过程中发生错误，则抛出异常。
     */
    Object check(String tk, CheckDeviceInfo checkDeviceInfo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 开始处理请求。此方法用于初始化和启动对传入的HTTP请求的处理流程。
     *
     * @param tk 客户端唯一标识，用于验证或识别客户端。
     * @param request 包含客户端数据的Servlet请求对象。
     * @param response 用于将处理结果返回给客户端的ServletResponse对象。
     * @return 返回一个对象，表示请求处理的结果。具体类型和内容取决于实现逻辑。
     */
    Object startRequest(String tk, HttpServletRequest request, HttpServletResponse response);

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
    Object dispatch(String tk, HttpServletRequest request, HttpServletResponse response) throws Exception;

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
    Object dispatchStream(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
