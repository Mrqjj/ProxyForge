package com.proxy.forge.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.net.InternetDomainName;
import com.proxy.forge.api.pojo.QueryById;
import com.proxy.forge.api.pojo.SaveWebSite;
import com.proxy.forge.api.pojo.SearchWebSite;
import com.proxy.forge.dto.Domain;
import com.proxy.forge.dto.WebSite;
import com.proxy.forge.repository.WebSiteRepository;
import com.proxy.forge.service.WebSiteService;
import com.proxy.forge.tools.GlobalStaticVariable;
import com.proxy.forge.vo.ResponseApi;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.proxy.forge.tools.GlobalStaticVariable.*;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 站点列表实现类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-28 12:26
 **/
@Slf4j
@Service
public class WebSiteServiceImpl implements WebSiteService {

    @Autowired
    WebSiteRepository webSiteRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 保存站点信息。
     *
     * @param saveWebSite 包含要保存的站点信息的对象，包括站点名称、域名ID、目标地址等必要字段
     * @return 返回一个对象，具体类型和内容取决于实现逻辑。在当前实现中总是返回null。
     */
    @Override
    public Object save(SaveWebSite saveWebSite) {
        //拷贝属性数据
        BeanCopier copier = BeanCopier.create(SaveWebSite.class, WebSite.class, false);
        WebSite webSite = new WebSite();
        copier.copy(saveWebSite, webSite, null);
        webSite.setCreateTime(LocalDateTime.now());
        int result = 0;
        if (webSite.getId() == null) {
            //新增  判断, 站点名称是否存在，或者目标网址是否存在
            if (webSiteRepository.queryWebSiteByNameOrTargetUrl(webSite) != null) {
                return new ResponseApi(201, WEBSITE_OR_TARGET_URL_EXISTS, null);
            }
            result = webSiteRepository.save(webSite).getId();
        } else {
            //编辑
            result = webSiteRepository.updateWebSiteById(webSite);
        }
        if (result > 0) {
            stringRedisTemplate.opsForValue().set(REDIS_WEBSITE_CACHE_KEY + saveWebSite.getDomain(), JSONObject.toJSONString(webSite));
            return new ResponseApi(200, API_MESSAGE_SUCCESS, null);
        } else {
            return new ResponseApi(201, API_MESSAGE_FAIL, null);
        }
    }

    /**
     * 获取站点列表。
     *
     * @param searchWebSite 用于搜索站点的参数，包括关键词、域名筛选条件、状态筛选条件、页码及每页数量等信息
     * @param request       包含客户端数据的 Servlet 请求
     * @param response      输出的servlet响应，用于将处理结果返回给客户端
     * @return 返回一个对象，表示查询到的站点列表。具体返回对象的类型和结构取决于实现逻辑
     */
    @Override
    public Object webSiteList(SearchWebSite searchWebSite, HttpServletRequest request, HttpServletResponse response) {
        Pageable pageable = PageRequest.of(
                searchWebSite.getPageNum() - 1,   // JPA 页码从 0 开始
                searchWebSite.getPageSize()
        );
        Specification<WebSite> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. keyword 模糊搜索 站点名称
            if (searchWebSite.getKeyWord() != null && !searchWebSite.getKeyWord().isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + searchWebSite.getKeyWord() + "%"));
            }

            //2. 域名 等与查询
            if (StringUtils.isNotBlank(searchWebSite.getDomain())) {
                predicates.add(cb.equal(root.get("domain"), searchWebSite.getDomain()));
            }

            // 3. status 等于查询
            if (searchWebSite.getStatus() != null && !searchWebSite.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), searchWebSite.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<WebSite> webSites = webSiteRepository.findAll(spec, pageable).getContent();
        return new ResponseApi(200, "获取成功.", webSites, webSiteRepository.count());
    }

    /**
     * 根据提供的查询对象获取站点的详细信息。
     *
     * @param query 包含要查询的站点ID的对象
     * @return 返回一个对象，表示查询到的站点详情。具体返回对象的类型和结构取决于实现逻辑。在当前实现中总是返回null。
     */
    @Override
    public Object webSiteDetail(QueryById query) {
        Optional<WebSite> webSite = webSiteRepository.findById(query.getId());
        if (webSite.isEmpty()) {
            return new ResponseApi(201, WEBSITE_DETAIL_NO_EXISTS, null);
        } else {
            return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, webSite.get());
        }
    }

    /**
     * 删除指定ID的网站。
     *
     * @param query 包含要删除的站点ID的对象
     * @return 返回一个对象，表示删除操作的结果。具体返回对象的类型和结构取决于实现逻辑。在当前实现中总是返回null。
     */
    @Override
    public Object deleteWebSite(QueryById query) {
        webSiteRepository.deleteById(query.getId());
        stringRedisTemplate.opsForValue().getAndDelete(REDIS_WEBSITE_CACHE_KEY + query.getDomain());
        return new ResponseApi(200, API_MESSAGE_SUCCESS, null);
    }

    /**
     * 获取指定服务器名称的网站配置信息。
     *
     * @param serverName 服务器的名称
     * @return 返回一个对象，表示获取到的网站配置信息。具体返回对象的类型和结构取决于实现逻辑。
     */
    @Override
    public Object getWebSiteConfig(String serverName) {
        // 读取web配置
        String websiteStr = stringRedisTemplate.opsForValue().get(REDIS_WEBSITE_CACHE_KEY + serverName);
        if (StringUtils.isBlank(websiteStr)) {
            InternetDomainName idn = InternetDomainName.from(serverName);
            if (idn.isUnderPublicSuffix()) {
                websiteStr = stringRedisTemplate.opsForValue().get(REDIS_WEBSITE_CACHE_KEY + "*." + idn.topPrivateDomain());
            }
        }
        if (StringUtils.isBlank(websiteStr)) {
            log.info("[获取站点配置]:  请求主机名: [{}], 没有找到站点配置, 返回到全局跳转页面", serverName);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseApi(403, "error", "data error"));
        }
        // 读取站点的配置文件
        return JSONObject.parseObject(websiteStr, WebSite.class);
    }

    /**
     * 初始化所有网站的配置信息。
     *
     * @return 返回一个对象，表示初始化操作的结果。在当前实现中总是返回null。
     */
    @Override
    public int initAllWebSiteConfig() {
        List<WebSite> webSites = webSiteRepository.findAll();
        int success = 0;
        for (WebSite webSite : webSites) {
            if (webSite.getStatus().equalsIgnoreCase("running")) {
                stringRedisTemplate.opsForValue().set(REDIS_WEBSITE_CACHE_KEY + webSite.getDomain(), JSONObject.toJSONString(webSite));
                success += 1;
            }
        }
        return success;
    }
}
