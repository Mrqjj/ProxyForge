package com.proxy.forge.service;

import com.proxy.forge.api.pojo.QueryById;
import com.proxy.forge.api.pojo.SaveWebSite;
import com.proxy.forge.api.pojo.SearchWebSite;
import com.proxy.forge.dto.WebSite;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 站点列表接口类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-28 12:25
 **/
public interface WebSiteService {

    /**
     * 保存或更新站点信息。
     *
     * @param saveWebSite 包含待保存站点信息的对象，包含站点名称、域名ID、目标地址、状态等信息
     * @return 返回一个对象，表示保存操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    Object save(SaveWebSite saveWebSite);

    /**
     * 获取站点列表。
     *
     * @param searchWebSite 用于搜索站点的参数，包括关键词、域名筛选条件、状态筛选条件、页码及每页数量等信息
     * @param request       包含客户端数据的 Servlet 请求
     * @param response      输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示查询到的站点列表。具体返回对象的类型和结构取决于实现逻辑
     */
    Object webSiteList(SearchWebSite searchWebSite, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据提供的查询条件获取指定站点的详细信息。
     *
     * @param query 包含查询所需ID的对象，用于定位特定站点记录
     * @return 返回一个对象，表示所查询站点的详细信息。具体返回对象的类型和结构取决于实现逻辑
     */
    Object webSiteDetail(QueryById query);

    /**
     * 删除指定ID的网站。
     *
     * @param query 包含待删除网站ID的对象
     * @return 返回一个对象，表示删除操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    Object deleteWebSite(QueryById query);

    /**
     * 获取指定服务器名称的网站配置信息。
     *
     * @param serverName 服务器的名称
     * @return 返回一个对象，表示所查询到的网站配置信息。具体返回对象的类型和结构取决于实现逻辑
     */
    Object getWebSiteConfig(String serverName);
}
