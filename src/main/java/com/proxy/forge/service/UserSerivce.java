package com.proxy.forge.service;

import com.proxy.forge.api.pojo.UserLogin;
import com.proxy.forge.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 用户表接口表</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-24 21:58
 **/
public interface UserSerivce {


    /**
     * 根据用户ID更新用户信息。
     *
     * @param user 包含待更新信息的User对象。需要确保User对象中的id字段已正确设置，以便识别要更新的记录。
     * @return 返回一个整数值，表示受影响的行数。如果返回值大于0，表示至少有一行被成功更新；如果返回值为0，则表示没有找到匹配的记录或更新操作未改变任何数据。
     */
    int updateUserById(User user);


    /**
     * 初始化用户数据。此方法用于准备或重置用户相关的数据存储或状态。
     *
     * @return 返回一个整数值，表示初始化操作的结果。具体返回值的含义取决于实现逻辑。
     */
    int initUserData();

    /**
     * 处理用户登录请求。 statusCode = 200成功, 否则返回具体字段信息
     *
     * @param userLogin  提交参数
     * @param request  包含客户端数据的 Servlet 请求，如用户名和密码等认证信息。
     * @param response 输出的servlet响应，用于将处理结果返回给客户端，例如设置会话cookie或重定向。
     * @return 返回一个ResponseEntity对象，表示已成功登录的用户信息。如果登录失败，则返回null。
     */
    ResponseEntity<?> login(UserLogin userLogin, HttpServletRequest request, HttpServletResponse response);
}

