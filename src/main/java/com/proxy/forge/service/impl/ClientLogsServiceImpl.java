package com.proxy.forge.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.api.pojo.GeoCountry;
import com.proxy.forge.dto.ClientLogs;
import com.proxy.forge.repository.ClientLogsRepository;
import com.proxy.forge.service.ClientLogsService;
import com.proxy.forge.tools.GlobalStaticVariable;
import com.proxy.forge.vo.ResponseApi;
import com.proxy.forge.vo.StatResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 日志功能实现类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-03 16:11
 **/
@Service
public class ClientLogsServiceImpl implements ClientLogsService {

    @Autowired
    ClientLogsRepository clientLogsRepository;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 保存客户端日志信息。
     *
     * @param clientLogs 客户端日志对象，包含终端用户唯一标识、事件名称、请求目标路径等详细信息
     * @return 返回一个整数，表示操作的状态或影响的行数；具体含义取决于实现逻辑
     */
    @Override
    public int saveClientLogs(ClientLogs clientLogs) {
        // 推送到队列, 方便取日志推送到前端。
        stringRedisTemplate.opsForList().leftPush(GlobalStaticVariable.REDIS_WEBSITE_LOGS_KEY + clientLogs.getWebsiteId(), JSONObject.toJSONString(clientLogs));
        return clientLogsRepository.save(clientLogs).getId();
    }

    /**
     *
     * @param geoCountry 包含日志起始和结束时间的 GeoCountry 对象。该对象不得为空，且 startTime 和 endTime 字段必须非空。
     * @return
     */
    @Override
    public Object logGeoCountry(GeoCountry geoCountry) {
        ZoneId zone = ZoneId.of("Asia/Shanghai"); // 当前系统时区

        LocalDateTime start = geoCountry.getStartTime()
                .atZoneSameInstant(zone)   // 转到本地时区
                .toLocalDateTime();        // 转为 LocalDateTime 查询数据库

        LocalDateTime end = geoCountry.getEndTime()
                .atZoneSameInstant(zone)
                .toLocalDateTime();
        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, clientLogsRepository.logGeoCountry(start, end));
    }

    @Override
    public Object logGeoCity(GeoCountry geoCountry) {

        ZoneId zone = ZoneId.of("Asia/Shanghai"); // 当前系统时区

        LocalDateTime start = geoCountry.getStartTime()
                .atZoneSameInstant(zone)   // 转到本地时区
                .toLocalDateTime();        // 转为 LocalDateTime 查询数据库

        LocalDateTime end = geoCountry.getEndTime()
                .atZoneSameInstant(zone)
                .toLocalDateTime();
        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, clientLogsRepository.logGeoCity(start, end));
    }

    /**
     * 根据提供的地理国家信息检索指定域名的统计数据。
     *
     * @param geoCountry 地理国家信息，必须验证。该参数用于过滤领域统计量。
     * @return 包含与域名相关的统计数据的对象，按所给地理国家进行筛选。该对象的具体结构取决于实现方式
     * 以及领域统计的具体要求。
     */
    @Override
    public Object domainStats(GeoCountry geoCountry) {
        ZoneId zone = ZoneId.of("Asia/Shanghai"); // 当前系统时区

        LocalDateTime start = geoCountry.getStartTime()
                .atZoneSameInstant(zone)   // 转到本地时区
                .toLocalDateTime();        // 转为 LocalDateTime 查询数据库

        LocalDateTime end = geoCountry.getEndTime()
                .atZoneSameInstant(zone)
                .toLocalDateTime();
        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, clientLogsRepository.domainStats(start, end));
    }

    /**
     * 根据提供的地理国家信息检索趋势统计数据。
     *
     * @param geoCountry 包含日志起始和结束时间的 GeoCountry 对象。该对象不得为空，且 startTime 和 endTime 字段必须非空。
     * @return 包含趋势统计数据的对象。该对象的具体结构取决于实现方式以及趋势统计的具体要求。
     */
    @Override
    public Object trendStats(GeoCountry geoCountry) {
        ZoneId zone = ZoneId.of("Asia/Shanghai"); // 当前系统时区

        LocalDateTime start = geoCountry.getStartTime()
                .atZoneSameInstant(zone)   // 转到本地时区
                .toLocalDateTime();        // 转为 LocalDateTime 查询数据库

        LocalDateTime end = geoCountry.getEndTime()
                .atZoneSameInstant(zone)
                .toLocalDateTime();
        List<Object[]> pvList = clientLogsRepository.statDailyPv(start, end);
        List<Object[]> uvList = clientLogsRepository.statDailyUv(start, end);
        Long totalUv = clientLogsRepository.statTotalUv(start, end);

        Map<String, Long> pvMap = pvList.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));

        Map<String, Long> uvMap = uvList.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));

        List<String> dates = new ArrayList<>();
        List<Long> pv = new ArrayList<>();
        List<Long> uv = new ArrayList<>();

        LocalDate curr = start.toLocalDate();
        LocalDate last = end.toLocalDate();

        while (!curr.isAfter(last)) {
            String day = curr.toString(); // yyyy-MM-dd

            dates.add(curr.getMonthValue() + "/" + curr.getDayOfMonth()); // 11/27 格式

            pv.add(pvMap.getOrDefault(day, 0L));
            uv.add(uvMap.getOrDefault(day, 0L));

            curr = curr.plusDays(1);
        }

        return new ResponseApi(200, GlobalStaticVariable.API_MESSAGE_SUCCESS, new StatResultVO(dates, pv, uv, totalUv));
    }
}
