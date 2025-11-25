package com.proxy.forge.service;

import com.proxy.forge.dto.User;
import org.springframework.data.repository.query.Param;

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
}
