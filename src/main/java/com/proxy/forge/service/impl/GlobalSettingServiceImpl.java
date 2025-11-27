package com.proxy.forge.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.api.pojo.GlobalSettings;
import com.proxy.forge.service.GlobalSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 全局配置参数</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 15:47
 **/
@Slf4j
@Service
public class GlobalSettingServiceImpl implements GlobalSettingService {


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean initGlobalSetting() {
        String setting = stringRedisTemplate.opsForValue().get("globalSettings");
        if (StringUtils.isNotBlank(setting)) {
            log.info("[初始化全局配置]   已存在,不初始化.");
            return false;
        }
        GlobalSettings globalSettings = new GlobalSettings();
        globalSettings.setAnalyticsCode("");
        globalSettings.setCacheTime(60 * 60);
        globalSettings.setAutoRenew(true);
        globalSettings.setRenewDays(10);
        globalSettings.setDefaultUrl("https://www.google.com");
        stringRedisTemplate.opsForValue().set("globalSettings", JSON.toJSONString(globalSettings));
        return true;
    }

    /**
     * 从 Redis 缓存中获取全局设置，并以 GlobalSettings 对象的形式返回。
     * 如果缓存中找不到设置，根据实现不同，可能会返回空或默认的 GlobalSettings 对象。
     *
     * @return 将全局设置作为 GlobalSettings 对象
     */
    @Override
    public GlobalSettings getGlobalSetting() {
        String setting = stringRedisTemplate.opsForValue().get("globalSettings");
        return JSONObject.parseObject(setting, GlobalSettings.class);
    }

    /**
     * 保存全局设置到存储中。
     *
     * @param globalSettings 待保存的全局设置对象
     * @return 如果成功保存则返回true，否则返回false
     */
    @Override
    public boolean saveGlobalSettings(GlobalSettings globalSettings) {
        stringRedisTemplate.opsForValue().set("globalSettings", JSON.toJSONString(globalSettings));
        return true;
    }
}
