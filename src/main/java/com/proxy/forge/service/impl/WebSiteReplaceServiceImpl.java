package com.proxy.forge.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.api.pojo.DeleteCustomContent;
import com.proxy.forge.api.pojo.QueryCustomContent;
import com.proxy.forge.api.pojo.SaveCustomContent;
import com.proxy.forge.api.pojo.SaveWebSite;
import com.proxy.forge.dto.WebSite;
import com.proxy.forge.dto.WebSiteReplace;
import com.proxy.forge.repository.WebSiteReplaceRepository;
import com.proxy.forge.service.WebSiteReplaceService;
import com.proxy.forge.tools.GlobalStaticVariable;
import com.proxy.forge.vo.ResponseApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 站点替换接口实现类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-23 01:52
 **/

@Slf4j
@Service
public class WebSiteReplaceServiceImpl implements WebSiteReplaceService {

    @Autowired
    WebSiteReplaceRepository webSiteReplaceRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 站点替换接口，用于处理网站内容的替换逻辑。
     *
     * @param customContent 自定义响应内容配置对象，包含分页信息和目标站点主机名
     * @param request       包含客户端数据的 Servlet 请求
     * @param response      输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示站点替换操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */

    @Override
    public Object webSiteReplaceList(QueryCustomContent customContent, HttpServletRequest request, HttpServletResponse response) {
        Pageable pageable = PageRequest.of(
                customContent.getPageNum() - 1,   // JPA 页码从 0 开始
                customContent.getPageSize()
        );
        List<WebSiteReplace> webSiteReplaceList = webSiteReplaceRepository.findAll(pageable).getContent();
        ;
        return new ResponseApi(200, "获取成功.", webSiteReplaceList, webSiteReplaceRepository.count());
    }

    /**
     * 保存自定义内容。
     *
     * @param saveCustomContent 包含要保存的自定义内容信息的对象，包括网站主机名、URL路径、是否下载、文件名、内容、内容类型和状态
     * @param request           包含客户端数据的 Servlet 请求
     * @param response          输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示保存操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    @Override
    public Object saveCustomContent(SaveCustomContent saveCustomContent, HttpServletRequest request, HttpServletResponse response) {
        //拷贝属性数据
        BeanCopier copier = BeanCopier.create(SaveCustomContent.class, WebSiteReplace.class, false);
        WebSiteReplace webSiteReplace = new WebSiteReplace();
        copier.copy(saveCustomContent, webSiteReplace, null);
        if (saveCustomContent.getId() == null) {
            webSiteReplace.setAddTime(LocalDateTime.now());
            // save
            int id = webSiteReplaceRepository.save(webSiteReplace).getId();
            if (id > 0) {
                stringRedisTemplate.opsForValue().set(GlobalStaticVariable.WEBSITE_REPLACE_CONTENT_KEY + webSiteReplace.getWebSiteHost() + webSiteReplace.getUrlPath(), JSONObject.toJSONString(webSiteReplace));
                return new ResponseApi(200, "success", null);
            } else {
                return new ResponseApi(201, "error", null);
            }
        } else {
            // update
            int res = webSiteReplaceRepository.updateById(webSiteReplace);
            if (res > 0) {
                stringRedisTemplate.opsForValue().set(GlobalStaticVariable.WEBSITE_REPLACE_CONTENT_KEY + webSiteReplace.getWebSiteHost() + webSiteReplace.getUrlPath(), JSONObject.toJSONString(webSiteReplace));
                return new ResponseApi(200, "success", null);
            } else {
                return new ResponseApi(201, "error", null);
            }
        }
    }

    /**
     * 删除自定义站点替换内容。
     *
     * @param deleteCustomContent 包含要删除的自定义内容ID的对象
     * @param request             包含客户端数据的 Servlet 请求
     * @param response            输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示删除操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    @Override
    public Object deleteCustomContent(DeleteCustomContent deleteCustomContent, HttpServletRequest request, HttpServletResponse response) {
        WebSiteReplace webSiteReplace = webSiteReplaceRepository.queryWebSiteReplaceById(deleteCustomContent.getId());
        if (webSiteReplace != null){
            stringRedisTemplate.delete(GlobalStaticVariable.WEBSITE_REPLACE_CONTENT_KEY + webSiteReplace.getWebSiteHost() + webSiteReplace.getUrlPath());
        }
        webSiteReplaceRepository.deleteById(deleteCustomContent.getId());
        return new ResponseApi(200, "success", null);
    }
}
