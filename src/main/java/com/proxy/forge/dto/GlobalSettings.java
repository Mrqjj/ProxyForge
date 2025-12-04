package com.proxy.forge.dto;

import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.dto</p>
 * <p>Description: 全局配置</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 15:29
 **/
@Data
public class GlobalSettings {

    // 全局跳转链接
    private String defaultUrl;

    // 三方统计代码
    private String analyticsCode;

    // 缓存时间
    private int cacheTime;

    // 证书是否自动续签
    private Boolean autoRenew;

    // 到期前多少天自动续签证书
    private int renewDays;
}
