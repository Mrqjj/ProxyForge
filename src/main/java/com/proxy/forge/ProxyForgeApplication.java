package com.proxy.forge;

import com.proxy.forge.service.InitService;
import com.proxy.forge.service.UserSerivce;
import com.proxy.forge.service.impl.InitServiceImpl;
import com.proxy.forge.service.impl.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge</p>
 * <p>Description: main class</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-19 16:31
 **/
@EnableScheduling
@EnableAsync
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class ProxyForgeApplication {


    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        ConfigurableApplicationContext run = SpringApplication.run(ProxyForgeApplication.class, args);
        // 初始化操作
        InitServiceImpl initService = (InitServiceImpl) run.getBean(InitService.class);
        initService.init();
    }

}
