package com.proxy.forge.service.impl;

import com.proxy.forge.dto.User;
import com.proxy.forge.repository.UserRepository;
import com.proxy.forge.service.UserSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 用户功能实现表</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-24 21:59
 **/

@Slf4j
@Service
public class UserServiceImpl implements UserSerivce {

    @Autowired
    UserRepository repository;

    /**
     * 初始化用户数据。此方法用于准备或重置用户相关的数据存储或状态。
     *
     * @return 返回一个整数值，表示初始化操作的结果。具体返回值的含义取决于实现逻辑。
     */
    @Override
    public int initUserData() {
        int userCount = repository.queryAllCount();
        log.info("用户表是否需要初始化: {}", userCount == 0);
        if (userCount == 0) {
            String adminUserName = "admin";
            String adminUserPass = "admin";
            log.info("初始化管理员账号: {}, 密码: {}", adminUserName, adminUserPass);
            User u = new User();
            u.setUserName(adminUserName);
            u.setPassWord(adminUserPass);
            u.setType(1);
            u.setStatus(1);
            LocalDateTime date = LocalDateTime.now();
            u.setCreateTime(date);
            u.setExpiredTime(date.plusYears(10));
            return repository.save(u).getId();
        }
        return -1;
    }
}
