package com.proxy.forge.api.pojo;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 保存站点信息</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-28 12:22
 **/
@Data
public class SaveWebSite {

    private Integer id;     // 站点ID（编辑时有值）

    @NotBlank(message = "不允许null")
    private String name;    // 站点名称
    @NotBlank(message = "不允许为null")
    private String domain;     // 域名ID ⬅️ 这里
    @NotBlank(message = "不允许null")
    private String targetUrl;     // 目标地址
    @NotBlank(message = "不允许null")
    private String status;        // 状态: running/stopped/maintenance

    // 访问策略
    private Boolean allowPc;  //允许pc端访问

    private Boolean allowMobile;

    private Boolean allowAndroid;

    private Boolean allowIos;

    private String allowCountries; // 国家代码，逗号分隔，如 "CN,US,JP"

    // 移动端检测
    private Boolean checkBatteryCharging;
    private Boolean checkGpu;
    private Boolean checkPlatform;
    private Boolean checkAutomation;
    private Boolean checkSensor;

    // 插件信息
    private String pluginName;
    private String pluginPath;
    private String pluginVersion;
    private Long pluginSize;
}
