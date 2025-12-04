package com.proxy.forge.service;

import com.proxy.forge.api.pojo.QueryById;
import com.proxy.forge.api.pojo.SaveGlobalReplace;
import com.proxy.forge.dto.GlobalReplace;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 全局拦截定义接口</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 16:31
 **/
public interface GlobalReplaceService {

    /**
     * 初始化全局替换配置。
     *
     * @return 如果初始化成功返回true，否则返回false。
     */
    boolean initGlobalReplace();

    /**
     * 获取全局替换配置。
     *
     * @param request 包含客户端数据的 Servlet 请求。
     * @param response 输出的servlet响应，用于将处理结果返回给客户端。
     * @return 返回一个对象，表示全局替换配置的结果。具体类型和内容取决于实现逻辑。
     */
    Object getGlobalReplace(HttpServletRequest request, HttpServletResponse response);

    /**
     * 保存或更新全局替换配置。
     *
     * @param saveGlobalReplace 包含要保存的全局替换配置信息的对象。
     * @param request            服务器接收到的HTTP请求对象。
     * @param response           服务器发送回客户端的HTTP响应对象。
     * @return 返回一个表示操作结果的对象。
     */
    Object saveGlobalReplace(SaveGlobalReplace saveGlobalReplace, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据给定的URI获取全局替换配置。
     *
     * @param uri 用于查找全局替换配置的URI。
     * @return 返回与指定URI关联的GlobalReplace对象，如果未找到匹配项，则返回null。
     */
    GlobalReplace getGlobalReplace(String uri);

    /**
     * 根据给定的ID删除全局替换配置。
     *
     * @param deleteById 包含要删除的全局替换配置ID的信息对象。
     * @return 返回一个表示操作结果的对象，具体类型和内容取决于实现逻辑。
     */
    Object deleteGlobalReplace(QueryById deleteById);
}
