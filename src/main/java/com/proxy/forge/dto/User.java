package com.proxy.forge.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.dto</p>
 * <p>Description: 用户表</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-24 18:12
 **/
@Data
@Table(name = "user")
@Entity
public class User implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    //用户名
    @Column(name = "user_name", columnDefinition = "varchar(255) comment '用户名'")
    private String userName;

    //用户密码
    @Column(name = "pass_word", columnDefinition = "varchar(255) comment '用户密码'")
    private String passWord;

    // 账号状态
    @Column(name = "status", columnDefinition = "int default 1 comment '账户状态, 默认1正常, 2锁定无效'")
    private int status;

    // 账号类型
    @Column(name = "type", columnDefinition = "int default 1 comment '账户类型, 1超级管理员, 2 普通用户'")
    private int type;

    // 创建时间
    @Column(name = "create_time", columnDefinition = "datetime default CURRENT_TIMESTAMP comment '数据创建时间'")
    private LocalDateTime createTime;

    // 数据过期时间
    @Column(name = "expired_time", columnDefinition = "datetime default CURRENT_TIMESTAMP comment '账号过期时间'")
    private LocalDateTime expiredTime;
}
