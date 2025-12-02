package com.proxy.forge.service;

import com.proxy.forge.api.pojo.QueryById;
import com.proxy.forge.api.pojo.SaveApiKey;
import com.proxy.forge.api.pojo.SearchApiKey;
import com.proxy.forge.dto.ApiKeys;

import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-01 22:05
 **/
public interface ApiKeysService {

    /**
     * 获取API密钥列表。
     *
     * @param searchApiKey 搜索API密钥的参数，包括关键词、状态、页码及每页数量等信息
     * @return 返回一个包含符合条件的API密钥列表
     */
    Object apiKeyList(SearchApiKey searchApiKey);

    /**
     * 保存或更新API密钥信息。
     *
     * @param saveApiKey 包含待保存的API密钥信息的对象，包括初始额度、剩余额度、API密钥、提供者、备注及是否激活等信息
     * @return 返回一个对象，表示保存操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    Object saveApiKey(SaveApiKey saveApiKey);

    /**
     * 获取指定提供商的缓存激活API密钥列表。
     *
     * @param providerName 提供商名称，用于筛选属于该提供商的激活API密钥
     * @return 返回一个包含激活状态的API密钥列表，这些密钥属于指定的提供商
     */
    List<ApiKeys> getCachedActiveKeys(String providerName);

    /**
     * 删除指定ID的API密钥。
     *
     * @param query 包含待删除API密钥ID的对象
     * @return 返回一个对象，表示删除操作的结果。具体返回对象的类型和结构取决于实现逻辑
     */
    Object deleteApiKey(QueryById query);
}
