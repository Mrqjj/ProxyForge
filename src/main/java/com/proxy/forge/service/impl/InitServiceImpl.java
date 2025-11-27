package com.proxy.forge.service.impl;

import com.proxy.forge.service.GlobalReplaceService;
import com.proxy.forge.service.GlobalSettingService;
import com.proxy.forge.service.InitService;
import com.proxy.forge.service.UserSerivce;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 初始化服务实现类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-24 23:17
 **/

@Slf4j
@Service
public class InitServiceImpl implements InitService {

    @Autowired
    UserSerivce userSerivce;
    @Autowired
    GlobalSettingService globalSettingService;
    @Autowired
    GlobalReplaceService globalReplaceService;

    @Override
    public void init() {
        // 初始化 用户表数据
        log.info("[初始化用户表数据]  结果: {}", userSerivce.initUserData() > 0 ? "成功" : userSerivce.initUserData() == -1 ? "非首次,不初始化" : "初始化失败");
        log.info("[初始化全局配置] 结果: {}", globalSettingService.initGlobalSetting());
        log.info("[初始化全局拦截规则]  结果: {}",globalReplaceService.initGlobalReplace());
    }
}
