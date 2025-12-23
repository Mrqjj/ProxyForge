package com.proxy.forge.service;

import com.proxy.forge.api.pojo.DeleteCustomContent;
import com.proxy.forge.api.pojo.QueryCustomContent;
import com.proxy.forge.api.pojo.SaveCustomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 站点替换接口</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-23 01:51
 **/
public interface WebSiteReplaceService {


    /**
     * 站点替换接口，用于处理网站内容的替换逻辑。
     *
     * @param customContent 自定义响应内容配置对象，包含分页信息和目标站点主机名
     * @param request 包含客户端数据的 Servlet 请求
     * @param response 输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示站点替换操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    Object webSiteReplaceList(QueryCustomContent customContent, HttpServletRequest request, HttpServletResponse response);

    /**
     * 保存自定义内容。
     *
     * @param saveCustomContent 包含要保存的自定义内容信息的对象，包括网站主机名、URL路径、是否下载、文件名、内容、内容类型和状态
     * @param request 包含客户端数据的 Servlet 请求
     * @param response 输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示保存操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    Object saveCustomContent(SaveCustomContent saveCustomContent, HttpServletRequest request, HttpServletResponse response);

    /**
     * 删除自定义站点替换内容。
     *
     * @param deleteCustomContent 包含要删除的自定义内容ID的对象
     * @param request 包含客户端数据的 Servlet 请求
     * @param response 输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示删除操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    Object deleteCustomContent(DeleteCustomContent deleteCustomContent, HttpServletRequest request, HttpServletResponse response);
}
