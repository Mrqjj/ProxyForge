package com.proxy.forge.repository;

import com.proxy.forge.dto.GlobalReplace;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.repository</p>
 * <p>Description: 全局拦截替换操作</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 16:30
 **/

@Repository
public interface GlobalReplaceRepository extends JpaRepository<GlobalReplace, Integer> {

    /**
     * 通过其URL模式找到全局替换配置。
     *
     * @param urlPattern 搜索URL模式
     * @return 匹配给定URL模式的GlobalReplace实体，若未匹配则为空
     */
    @Query(value = "select g from GlobalReplace  g where g.urlPattern=:urlPattern")
    GlobalReplace findGlobalReplaceByUrlPattern(@Param("urlPattern") String urlPattern);

    /**
     * 更新指定URL模式的全局替换配置。
     *
     * 该方法根据给定的GlobalReplace对象更新数据库中匹配特定URL模式的记录。只有在提供的GlobalReplace对象中非空字段将被更新。
     *
     * @param globalReplace 包含更新信息的GlobalReplace对象
     * @return 受更新操作影响的行数
     */
    @Transactional
    @Modifying
    @Query(value = "update GlobalReplace g set " +
            "g.urlPattern=COALESCE(:#{#repalce.urlPattern},g.urlPattern)," +
            "g.contentType= COALESCE(:#{#repalce.contentType},g.contentType)," +
            "g.responseContent= COALESCE(:#{#repalce.responseContent},g.responseContent)" +
            "where g.urlPattern=:#{#repalce.urlPattern}")
    int updateGlobalReplaceByUrlPattern(@Param("repalce") GlobalReplace globalReplace);
}
