package com.proxy.forge.api.pojo;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 保存站点信息</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
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
    @NotNull(message = "allowMobile 不能为空")
    private Boolean allowPc;  //允许pc端访问

    @NotNull(message = "allowMobile 不能为空")
    private Boolean allowMobile;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean allowAndroid;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean allowIos;

    private String allowCountries; // 国家代码，逗号分隔，如 "CN,US,JP"

    // 移动端检测
    @NotNull(message = "allowMobile 不能为空")
    private Boolean checkBatteryCharging;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean checkGpu;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean checkPlatform;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean checkAutomation;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean checkSensor;

    // ip检测
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isAbuser;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isAnonymous;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isAttacker;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isBogon;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isCloudProvider;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isProxy;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isRelay;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isThreat;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isTor;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isTorExit;
    @NotNull(message = "allowMobile 不能为空")
    private Boolean isVpn;



    // 插件信息
    private String pluginName;
    private String pluginPath;
    private String pluginVersion;
    private Long pluginSize;
}
