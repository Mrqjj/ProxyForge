package com.proxy.forge.repository;

import com.proxy.forge.dto.ApiKeys;
import com.proxy.forge.dto.Domain;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.repository</p>
 * <p>Description: ApiKeys数据库操作</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-01 22:07
 **/
@Repository
public interface ApiKeysRepository extends JpaRepository<ApiKeys, Integer>, JpaSpecificationExecutor<ApiKeys> {


    @Transactional
    @Modifying
    @Query(value = "update ApiKeys keys set keys.provider=COALESCE(:#{#apiKeys.provider},keys.provider)," +
            "keys.apiKey=COALESCE(:#{#apiKeys.apiKey},keys.apiKey), " +
            "keys.creditsRemaining =COALESCE(:#{#apiKeys.creditsRemaining},keys.creditsRemaining), " +
            "keys.initialCredits =COALESCE(:#{#apiKeys.initialCredits},keys.initialCredits), " +
            "keys.isActive =COALESCE(:#{#apiKeys.isActive},keys.isActive), " +
            "keys.notes =COALESCE(:#{#apiKeys.notes},keys.notes) " +
            "where keys.id=:#{#apiKeys.id}")
    long updateApikeysById(@Param("apiKeys") ApiKeys apiKeys);

    /**
     * 查询并检索基于指定提供者名称的ApiKey列表。
     *
     * @param providerName 用来筛选ApiKey的API提供者名称
     * @return 与给定提供者名称匹配的ApiKey列表
     */
    @Query(value = "select k from ApiKeys k where k.provider=:providerName and k.isActive=true")
    List<ApiKeys> queryApiKeysByProvider(@Param("providerName") String providerName);
}
