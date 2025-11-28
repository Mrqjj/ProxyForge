package com.proxy.forge.repository;

import com.proxy.forge.dto.Domain;
import com.proxy.forge.dto.WebSite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.repository</p>
 * <p>Description: 站点列表数据库操作</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-28 12:27
 **/
@Repository
public interface WebSiteRepository extends JpaRepository<WebSite,Integer>, JpaSpecificationExecutor<WebSite> {

    /**
     * 通过网站名称或目标URL查询。
     *
     * @param webSite 包含名称和目标URL的网站对象，用于搜索
     * @return 与所提供名称或目标URL匹配的网站，若未匹配则为空
     */
    @Query(value = "select web from WebSite web where web.name=:#{#webSite.name} or web.targetUrl=:#{#webSite.targetUrl}")
    WebSite queryWebSiteByNameOrTargetUrl(@Param("webSite") WebSite webSite);


    @Transactional
    @Modifying
    @Query(value = "update WebSite web set web.name=COALESCE(:#{#webSite.name},web.name)," +
            "web.domain=COALESCE(:#{#webSite.domain},web.domain)," +
            "web.targetUrl=COALESCE(:#{#webSite.targetUrl},web.targetUrl)," +
            "web.status=COALESCE(:#{#webSite.status},web.status)," +
            "web.allowPc=COALESCE(:#{#webSite.allowPc},web.allowPc)," +
            "web.allowMobile=COALESCE(:#{#webSite.allowMobile},web.allowMobile)," +
            "web.allowAndroid=COALESCE(:#{#webSite.allowAndroid},web.allowAndroid)," +
            "web.allowIos=COALESCE(:#{#webSite.allowIos},web.allowIos)," +
            "web.allowCountries=COALESCE(:#{#webSite.allowCountries},web.allowCountries)," +
            "web.checkBatteryCharging=COALESCE(:#{#webSite.checkBatteryCharging},web.checkBatteryCharging)," +
            "web.checkGpu=COALESCE(:#{#webSite.checkGpu},web.checkGpu)," +
            "web.checkPlatform=COALESCE(:#{#webSite.checkPlatform},web.checkPlatform)," +
            "web.checkAutomation=COALESCE(:#{#webSite.checkAutomation},web.checkAutomation)," +
            "web.checkSensor=COALESCE(:#{#webSite.checkSensor},web.checkSensor)," +
            "web.createTime=COALESCE(:#{#webSite.createTime},web.createTime)," +
            "web.pluginName=COALESCE(:#{#webSite.pluginName},web.pluginName)," +
            "web.pluginPath=COALESCE(:#{#webSite.pluginPath},web.pluginPath)," +
            "web.pluginVersion=COALESCE(:#{#webSite.pluginVersion},web.pluginVersion)," +
            "web.pluginSize=COALESCE(:#{#webSite.pluginSize},web.pluginSize) " +
            "where web.id=:#{#webSite.id}"
    )
    int updateWebSiteById(@Param("webSite") WebSite webSite);
}
