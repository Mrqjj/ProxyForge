package com.proxy.forge.service;

import com.proxy.forge.api.pojo.GlobalSettings;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 全局配置</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 15:45
 **/
public interface GlobalSettingService {

    /**
     * 初始化全局配置
     *
     * @return  返回结果
     */
    boolean initGlobalSetting();

    /**
     * 获取全局配置信息。
     *
     * @return 返回当前的全局设置，包括但不限于默认跳转链接、统计代码、缓存时间等。
     */
    GlobalSettings getGlobalSetting();


    boolean saveGlobalSettings(GlobalSettings globalSettings);
}
