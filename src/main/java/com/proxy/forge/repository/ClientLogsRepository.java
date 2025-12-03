package com.proxy.forge.repository;

import com.proxy.forge.dto.ClientLogs;
import com.proxy.forge.vo.DomainStatVO;
import com.proxy.forge.vo.GeoCityResultVO;
import com.proxy.forge.vo.GeoContryResultVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.repository</p>
 * <p>Description: 日志数据库操作</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-03 16:17
 **/
@Repository
public interface ClientLogsRepository extends JpaRepository<ClientLogs, Integer> {

    /**
     * 查询指定时间段内，每个国家、省份和城市的日志记录数量。
     *
     * @param start 查询的开始时间，包括。
     * @param end 查询结束时间，包含。
     * @return GeoResultVO 对象列表，每个对象包含一个国家、省份、城市及其对应的日志记录数量，
     * 按计数由高至低排序。
     */
    @Query("""
                SELECT new com.proxy.forge.vo.GeoContryResultVO(
                    c.ipCountry,
                    COUNT(c)
                )
                FROM ClientLogs c
                WHERE (c.addTime BETWEEN :start AND :end)
                AND c.ipCountry != '-'
                GROUP BY c.ipCountry
                ORDER BY COUNT(c) DESC
            """)
    List<GeoContryResultVO> logGeoCountry(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query("""
                SELECT new com.proxy.forge.vo.GeoCityResultVO(
                    c.ipCountry,
                    c.ipProvince,
                    c.ipCity,
                    COUNT(c)
                )
                FROM ClientLogs c
                WHERE (c.addTime BETWEEN :start AND :end)
                AND c.ipCity != '-'
                GROUP BY c.ipCountry, c.ipProvince, c.ipCity
                ORDER BY COUNT(c) DESC
            """)
    List<GeoCityResultVO> logGeoCity(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    /**
     * 根据提供的开始和结束时间检索域统计数据。
     *
     * @param start 查询的开始时间，包括。
     * @param end 查询结束时间，包含。
     * @return DomainStatVO 对象列表，每个对象包含一个域名和该域的日志数量，
     * 按计数由高至低排序。
     */
    @Query("""
    SELECT new com.proxy.forge.vo.DomainStatVO(
        c.serverName,
        COUNT(c)
    )
    FROM ClientLogs c
    WHERE c.addTime BETWEEN :start AND :end
    GROUP BY c.serverName
    ORDER BY COUNT(c) DESC
    """)
    List<DomainStatVO> domainStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 统计指定时间段内每天的页面访问量(PV)。
     *
     * @param startTime 查询的开始时间，包括。
     * @param endTime 查询结束时间，包含。
     * @return List<Object[]> 对象列表，每个数组包含两个元素：日期（格式为'YYYY-MM-DD'）和该日的PV数量。
     */
    @Query("""
    SELECT DATE_FORMAT(c.addTime, '%Y-%m-%d') AS day,
           COUNT(c.id) AS pv
    FROM ClientLogs c
    WHERE c.addTime BETWEEN :startTime AND :endTime
    GROUP BY day
    ORDER BY day
    """)
    List<Object[]> statDailyPv(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);


    /**
     * 统计指定时间段内每天的独立访客数量(UV)。
     *
     * @param startTime 查询的开始时间，包括。
     * @param endTime 查询结束时间，包含。
     * @return List<Object[]> 对象列表，每个数组包含两个元素：日期（格式为'YYYY-MM-DD'）和该日的UV数量。
     */
    @Query("""
    SELECT DATE_FORMAT(c.addTime, '%Y-%m-%d') AS day,
           COUNT(DISTINCT c.tk) AS uv
    FROM ClientLogs c
    WHERE c.addTime BETWEEN :startTime AND :endTime
    GROUP BY day
    ORDER BY day
    """)
    List<Object[]> statDailyUv(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间段内的总独立访客数量(UV)。
     *
     * @param startTime 查询的开始时间，包括。
     * @param endTime 查询结束时间，包含。
     * @return 指定时间段内的总独立访客数量。
     */
    @Query("""
    SELECT COUNT(DISTINCT c.tk)
    FROM ClientLogs c
    WHERE c.addTime BETWEEN :startTime AND :endTime
    """)
    Long statTotalUv(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

}
