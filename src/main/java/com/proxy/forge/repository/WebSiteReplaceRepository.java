package com.proxy.forge.repository;

import com.proxy.forge.dto.WebSiteReplace;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * <p>Description: 替换内容数据库层</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-23 01:49
 **/
@Repository
public interface WebSiteReplaceRepository extends JpaRepository<WebSiteReplace,Integer>, JpaSpecificationExecutor<WebSiteReplace> {


    /**
     * 更新指定ID的Web站点替换配置。
     *
     * @param webSiteReplace 包含更新信息的Web站点替换对象，必须包含有效的ID以识别要更新的记录
     * @return 受更新操作影响的行数
     */
    @Transactional
    @Modifying
    @Query(value = "update WebSiteReplace w set " +
            "w.webSiteHost=COALESCE(:#{#web.webSiteHost},w.webSiteHost)," +
            "w.urlPath= COALESCE(:#{#web.urlPath},w.urlPath)," +
            "w.download= COALESCE(:#{#web.download},w.download)," +
            "w.fileName= COALESCE(:#{#web.fileName},w.fileName)," +
            "w.content= COALESCE(:#{#web.content},w.content)," +
            "w.contentType= COALESCE(:#{#web.contentType},w.contentType)," +
            "w.status= COALESCE(:#{#web.status},w.status)" +
            "where w.id=:#{#web.id}")
    int updateById(@Param("web") WebSiteReplace webSiteReplace);

    /**
     * 根据给定的ID查询Web站点替换配置。
     *
     * @param id 要查询的Web站点替换配置的唯一标识符
     * @return 与指定ID关联的WebSiteReplace对象，如果未找到则返回null
     */
    @Query(value = "select w from WebSiteReplace w where w.id=:id")
    WebSiteReplace queryWebSiteReplaceById(@Param("id") Integer id);

    /**
     * 根据域名查询
     * @param host
     * @return
     */
    @Query(value = "select count(*) from WebSiteReplace w where w.webSiteHost=:host")
    int countByWebsiteHost(@Param("host") String host);

    @Query(value = "select w from WebSiteReplace w where w.webSiteHost=:host")
    Page<WebSiteReplace> findByWebSiteHost(@Param("host")String webSiteHost, Pageable pageable);
}
