package com.proxy.forge.repository;

import com.proxy.forge.api.pojo.SearchDomain;
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
 * <p>Description: 域名相关数据库操作</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-26 19:34
 **/
@Repository
public interface DomainRepository extends JpaRepository<Domain, Integer> , JpaSpecificationExecutor<Domain> {

    /**
     * 查询与给定Domain对象中域名匹配的Domain实体。
     *
     * @param domain 包含待查询域名信息的Domain对象
     * @return 如果找到匹配的Domain实体，则返回该实体；如果未找到匹配项，则返回null
     */
    @Query(value = "select d from Domain d where d.domain=:#{#domain.domain}")
    Domain queryDomainByDomain(@Param("domain") Domain domain);

    /**
     * 根据提供的Domain对象更新数据库中指定ID的域名信息。
     * 只有在提供的Domain对象中非空字段将被更新。
     * 域名通过提供的Domain对象的 id 字段来识别。
     *
     * @param domain 包含更新信息的Domain对象
     * @return 受更新操作影响的行数
     */
    @Transactional
    @Modifying
    @Query(value = "update Domain d set d.status=COALESCE(:#{#domain.status},d.status)," +
            "d.enableSsl=COALESCE(:#{#domain.enableSsl},d.enableSsl), " +
            "d.visits =COALESCE(:#{#domain.visits},d.visits), " +
            "d.createTime =COALESCE(:#{#domain.createTime},d.createTime), " +
            "d.certExpTime =COALESCE(:#{#domain.certExpTime},d.certExpTime), " +
            "d.method =COALESCE(:#{#domain.method},d.method), " +
            "d.remark =COALESCE(:#{#domain.remark},d.remark) " +
            "where d.id=:#{#domain.id}")
    int updateDomainById(@Param("domain") Domain domain);

    /**
     * 查询所有未被任何网站使用的可用域名。
     *
     * @return 返回一个包含所有未与任何WebSite关联的Domain对象的列表
     */
    @Query("select d from Domain d where d.domain not in (select web.domain from WebSite web)")
    List<Domain> queryAvailableDomains();
}
