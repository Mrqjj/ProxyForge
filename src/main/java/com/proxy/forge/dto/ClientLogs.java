package com.proxy.forge.dto;

import com.ip2location.IPResult;
import com.proxy.forge.tools.IPLocationUtils;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * <p>ProjectName: MirrorAmazonProject</p>
 * <p>PackageName: com.vivcms.mirror.pojo</p>
 * <p>Description: 操作日志记录表</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-02 21:48
 **/
@Entity
@Table(name = "client_logs")
@Data
public class ClientLogs {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Column(name = "tk", columnDefinition = "varchar(255) comment '终端用户唯一标识,过期时间为3天.'")
    private String tk;
    @Column(name = "action", columnDefinition = "varchar(255) comment '事件名称'")
    private String action;
    @Column(name = "path", columnDefinition = "text comment '请求目标路径'")
    private String path;
    @Column(name = "method", columnDefinition = "varchar(255) comment '请求方法,POST,GET'")
    private String method;
    @Column(name = "body", columnDefinition = "text comment '发送数据, POST 为body体,GET为url参数'")
    private String body;
    @Column(name = "description", columnDefinition = "text comment '描述信息字段'")
    private String description;
    @Column(name = "add_time", columnDefinition = "datetime default CURRENT_TIMESTAMP comment '日志记录时间'")
    private LocalDateTime addTime;
    @Column(name = "client_ip", columnDefinition = "varchar(255) comment '终端IP'")
    private String clientIp;
    @Column(name = "ip_country", columnDefinition = "varchar(255) comment 'ip所属国家'")
    private String ipCountry;
    @Column(name = "ip_province", columnDefinition = "varchar(255) comment 'ip所属国家洲/省'")
    private String ipProvince;
    @Column(name = "ip_city", columnDefinition = "varchar(255) comment 'ip所属国家城市'")
    private String ipCity;
    @Column(name = "server_name", columnDefinition = "varchar(255) comment '当前请求域'")
    private String serverName;
    @Column(name = "website_id", columnDefinition = "int comment '站点id'")
    private int websiteId;


    public ClientLogs(String tk, String action, String path, String method, String body,
                      String description, String clientIp,
                      String serverName, int websiteId) {
        IPResult result = IPLocationUtils.query(clientIp);
        this.tk = tk;
        this.action = action;
        this.path = path;
        this.method = method;
        this.body = body;
        this.description = description;
        this.addTime = LocalDateTime.now();
        this.clientIp = clientIp;
        this.ipCountry = result.getCountryShort();
        this.ipProvince = result.getRegion();
        this.ipCity = result.getCity();
        this.serverName = serverName;
        this.websiteId = websiteId;
    }

    public ClientLogs() {

    }
}
