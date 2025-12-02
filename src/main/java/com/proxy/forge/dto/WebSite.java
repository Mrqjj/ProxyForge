package com.proxy.forge.dto;

import jakarta.persistence.*;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;

import java.time.LocalDateTime;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.dto</p>
 * <p>Description: 站点数据库字段类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 23:54
 **/
@Table(name = "web_site")
@Data
@Entity
public class WebSite {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;     // 站点ID（编辑时有值）

    @Column(name = "name", columnDefinition = "varchar(255) comment '站点名称.'")
    private String name;    // 站点名称

    @Column(name = "domain", columnDefinition = "varchar(255) comment '域名.'")
    private String domain;     // 域名ID ⬅️ 这里

    @Column(name = "target_url", columnDefinition = "varchar(255) comment '反向代理的目标url.'")
    private String targetUrl;     // 目标地址

    @Column(name = "status", columnDefinition = "varchar(255) comment '站点状态'")
    private String status;        // 状态: running/stopped/maintenance

    // 访问策略
    @Column(name = "allow_pc", columnDefinition = "TINYINT comment '是否允许PC端访问'")
    private Boolean allowPc;  //允许pc端访问

    @Column(name = "allow_mobile", columnDefinition = "TINYINT comment '是否允许移动端访问'")
    private Boolean allowMobile;

    @Column(name = "allow_android", columnDefinition = "TINYINT comment '是否允许安卓访问'")
    private Boolean allowAndroid;

    @Column(name = "allow_ios", columnDefinition = "TINYINT comment '是否允许ios访问'")
    private Boolean allowIos;

    @Column(name = "allow_countries", columnDefinition = "varchar(512) comment '允许访问的国家代码'")
    private String allowCountries; // 国家代码，逗号分隔，如 "CN,US,JP"

    // 移动端检测
    @Column(name = "check_battery_charging", columnDefinition = "TINYINT comment '检查电量满电正在充电'")
    private Boolean checkBatteryCharging;
    @Column(name = "check_gpu", columnDefinition = "TINYINT comment '检查GPU'")
    private Boolean checkGpu;
    @Column(name = "check_platform", columnDefinition = "TINYINT comment '检查平台'")
    private Boolean checkPlatform;
    @Column(name = "check_automation", columnDefinition = "TINYINT comment '检查自动化'")
    private Boolean checkAutomation;
    @Column(name = "check_sensor", columnDefinition = "TINYINT comment '检查陀螺仪传感器 安卓检查， 苹果不检查'")
    private Boolean checkSensor;

    // ip检查开关
    @Column(name = "is_abuser", columnDefinition = "TINYINT comment '检测IP是否有滥用行为记录'")
    private Boolean isAbuser;
    @Column(name = "is_anonymous", columnDefinition = "TINYINT comment '检测IP是匿名访问'")
    private Boolean isAnonymous;
    @Column(name = "is_attacker", columnDefinition = "TINYINT comment '检测IP是否有攻击行为记录'")
    private Boolean isAttacker;
    @Column(name = "is_bogon", columnDefinition = "TINYINT comment '检测是否为无效/保留IP地址'")
    private Boolean isBogon;
    @Column(name = "is_cloud_provider", columnDefinition = "TINYINT comment '检测IP是否来自云服务提供商'")
    private Boolean isCloudProvider;
    @Column(name = "is_proxy", columnDefinition = "TINYINT comment '检测IP是否为代理服务器'")
    private Boolean isProxy;
    @Column(name = "is_relay", columnDefinition = "TINYINT comment '检测IP是否为中继节点'")
    private Boolean isRelay;
    @Column(name = "is_threat", columnDefinition = "TINYINT comment '检测IP是否存在安全威胁记录'")
    private Boolean isThreat;
    @Column(name = "is_tor", columnDefinition = "TINYINT comment '检测IP是否来自Tor网络'")
    private Boolean isTor;
    @Column(name = "is_tor_exit", columnDefinition = "TINYINT comment '检测IP是否为Tor出口节点'")
    private Boolean isTorExit;
    @Column(name = "is_vpn", columnDefinition = "TINYINT comment '检测IP是否使用VPN服务'")
    private Boolean isVpn;


    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP comment '创建时间'")
    private LocalDateTime createTime;
    @Column(name = "plugin_name", columnDefinition = "varchar(255) comment '插件名称'")
    private String pluginName;
    @Column(name = "plugin_path", columnDefinition = "varchar(255) comment '插件路径'")
    private String pluginPath;
    @Column(name = "plugin_version", columnDefinition = "varchar(255) comment '插件版本号'")
    private String pluginVersion;
    @Column(name = "plugin_size", columnDefinition = "bigint comment '插件大小'")
    private Long pluginSize;
}
