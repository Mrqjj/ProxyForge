package com.proxy.forge.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.api.pojo.UserLogin;
import com.proxy.forge.dto.User;
import com.proxy.forge.repository.UserRepository;
import com.proxy.forge.service.UserSerivce;
import com.proxy.forge.tools.JwtUtils;
import com.proxy.forge.vo.ResponseApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    UserRepository userRepository;

    /**
     * 根据用户ID更新用户信息。
     *
     * @param user 包含待更新信息的User对象。需要确保User对象中的id字段已正确设置，以便识别要更新的记录。
     * @return 返回一个整数值，表示受影响的行数。如果返回值大于0，表示至少有一行被成功更新；如果返回值为0，则表示没有找到匹配的记录或更新操作未改变任何数据。
     */
    @Override
    public int updateUserById(User user) {
        return userRepository.updateUserById(user);
    }

    /**
     * 初始化用户数据。此方法用于准备或重置用户相关的数据存储或状态。
     *
     * @return 返回一个整数值，表示初始化操作的结果。具体返回值的含义取决于实现逻辑。
     */
    @Override
    public int initUserData() {
        int userCount = userRepository.queryAllCount();
        log.info("用户表是否需要初始化: {}", userCount == 0);
        if (userCount == 0) {
            String adminUserName = "admin";
            String adminUserPass = RandomStringUtils.random(12,true,false);
            log.info("初始化管理员账号: {}, 密码: {}", adminUserName, adminUserPass);
            User u = new User();
            u.setUserName(adminUserName);
            u.setPassWord(adminUserPass);
            u.setType(1);
            u.setStatus(1);
            LocalDateTime date = LocalDateTime.now();
            u.setCreateTime(date);
            u.setExpiredTime(date.plusYears(10));
            return userRepository.save(u).getId();
        }
        return -1;
    }


    @Override
    public ResponseEntity<?> login(UserLogin userLogin, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        user.setUserName(userLogin.getUserName());
        user.setPassWord(userLogin.getPassWord());
        user.setStatus(1);
        User resUser = userRepository.findByUserNameAndPassWord(user);
        if (resUser != null) {
            //登录成功
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", JwtUtils.createToken(JSONObject.toJSONString(resUser)));
            JSONObject userInfoObj = new JSONObject();
            userInfoObj.put("userName", resUser.getUserName());
            userInfoObj.put("expiredTime", resUser.getExpiredTime());
            jsonObject.put("userInfo", userInfoObj);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(new ResponseApi(200, "登录成功", jsonObject));
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseApi(201, "用户名或密码错误", null));
    }
}
