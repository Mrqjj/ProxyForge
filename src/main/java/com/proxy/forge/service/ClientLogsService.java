package com.proxy.forge.service;

import com.proxy.forge.api.pojo.GeoCountry;
import com.proxy.forge.dto.ClientLogs;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 日志接口定义</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-03 16:11
 **/
public interface ClientLogsService {

    /**
     * 保存客户端日志信息到数据库。
     *
     * @param clientLogs 包含要保存的日志信息的对象。该对象应包含如终端用户唯一标识(tk)、事件名称(action)、请求目标路径(path)等字段。
     * @return 返回一个整数，表示受影响的行数。如果成功保存，则返回1或更多；如果未成功保存（例如，由于数据验证失败），则可能返回0或抛出异常。
     */
    int saveClientLogs(ClientLogs clientLogs);

    /**
     * 记录基于所提供的GeoCountry对象的地理国家信息。
     *
     * @param geoCountry 包含日志起始和结束时间的 GeoCountry 对象。该对象不得为空，且 startTime 和 endTime 字段必须非空。
     * @return 表示日志作结果的对象。该对象的具体类型和内容取决于实现方式。
     */
    Object logGeoCountry(GeoCountry geoCountry);
    Object logGeoCity(GeoCountry geoCountry);

    /**
     * 根据提供的地理国家信息检索指定域名的统计数据。
     *
     * @param geoCountry 地理国家信息，必须验证。该参数用于过滤领域统计量。
     * @return 包含与域名相关的统计数据的对象，按所给地理国家进行筛选。该对象的具体结构取决于实现方式
     * 以及领域统计的具体要求。
     */
    Object domainStats(GeoCountry geoCountry);

    /**
     * 根据提供的地理国家信息检索趋势统计数据。
     *
     * @param geoCountry 包含日志起始和结束时间的 GeoCountry 对象。该对象不得为空，且 startTime 和 endTime 字段必须非空。
     * @return 包含趋势统计数据的对象。该对象的具体结构取决于实现方式以及趋势统计的具体要求。
     */
    Object trendStats(GeoCountry geoCountry);
}
