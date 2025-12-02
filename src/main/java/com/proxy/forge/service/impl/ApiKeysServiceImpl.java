package com.proxy.forge.service.impl;

import com.proxy.forge.api.pojo.QueryById;
import com.proxy.forge.api.pojo.SaveApiKey;
import com.proxy.forge.api.pojo.SaveWebSite;
import com.proxy.forge.api.pojo.SearchApiKey;
import com.proxy.forge.dto.ApiKeys;
import com.proxy.forge.dto.Domain;
import com.proxy.forge.dto.WebSite;
import com.proxy.forge.repository.ApiKeysRepository;
import com.proxy.forge.service.ApiKeysService;
import com.proxy.forge.tools.GlobalStaticVariable;
import com.proxy.forge.vo.ResponseApi;
import jakarta.persistence.criteria.Predicate;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: apiKeys 数据库操作</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-01 22:06
 **/
@Service
public class ApiKeysServiceImpl implements ApiKeysService {

    @Autowired
    ApiKeysRepository apiKeysRepository;

    /**
     * 获取API密钥列表。
     *
     * @param searchApiKey 搜索API密钥的参数，包括关键词、状态、页码及每页数量等信息
     * @return 返回一个包含符合条件的API密钥列表
     */
    @Override
    public Object apiKeyList(SearchApiKey searchApiKey) {


        Sort sort = Sort.by(Sort.Direction.DESC, "isActive"); // 按 id 降序
        Pageable pageable = PageRequest.of(
                searchApiKey.getPageNum() - 1,   // JPA 页码从 0 开始
                searchApiKey.getPageSize(),
                sort
        );
        Specification<ApiKeys> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. keyword 模糊搜索
            if (searchApiKey.getKeyWord() != null && !searchApiKey.getKeyWord().isEmpty()) {
                String kw = "%" + searchApiKey.getKeyWord() + "%";
                Predicate p1 = cb.like(root.get("apiKey"), kw);
                Predicate p2 = cb.like(root.get("notes"), kw);
                predicates.add(cb.or(p1, p2)); // 关键是要 OR
            }

            // 3. status 等于查询
            if (searchApiKey.getStatus() != null) {
                predicates.add(cb.equal(root.get("isActive"), searchApiKey.getStatus()));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<ApiKeys> domains = apiKeysRepository.findAll(spec, pageable).getContent();
        return new ResponseApi(200, "获取成功.", domains, apiKeysRepository.count());
    }

    /**
     * 保存或更新API密钥信息。
     *
     * @param saveApiKey 包含待保存或更新的API密钥信息的对象，包括初始额度、剩余额度、API密钥字符串、提供者、备注以及是否激活等字段
     * @return 返回一个对象表示操作的结果
     */
    @Override
    public Object saveApiKey(SaveApiKey saveApiKey) {
        BeanCopier copier = BeanCopier.create(SaveApiKey.class, ApiKeys.class, false);
        ApiKeys apiKeys = new ApiKeys();
        copier.copy(saveApiKey, apiKeys, null);
        long result = 0;
        if (saveApiKey.getId() == null) {
            result = apiKeysRepository.save(apiKeys).getId();
        } else {
            apiKeys.setId(saveApiKey.getId());
            result = apiKeysRepository.updateApikeysById(apiKeys);
        }
        return new ResponseApi(200, result > 0 ? GlobalStaticVariable.API_MESSAGE_SUCCESS : GlobalStaticVariable.API_MESSAGE_FAIL, result > 0);
    }

    /**
     * 从缓存中获取指定提供者的激活API密钥列表。
     *
     * @param providerName 提供者的名称
     * @return 返回一个包含指定提供者的所有激活API密钥的列表
     */
    @Override
    public List<ApiKeys> getCachedActiveKeys(String providerName) {
        return apiKeysRepository.queryApiKeysByProvider(providerName);
    }

    /**
     * 删除指定ID的API密钥。
     *
     * @param query 包含待删除API密钥ID的对象
     * @return 返回一个包含操作状态码、消息以及数据的对象，其中数据部分为null表示删除成功
     */
    @Override
    public Object deleteApiKey(QueryById query) {
        apiKeysRepository.deleteById(query.getId());
        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, null);
    }
}
