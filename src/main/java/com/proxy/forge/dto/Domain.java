package com.proxy.forge.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.dto</p>
 * <p>Description: 域名列表管理</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-26 19:18
 **/
@Data
@Table(name = "domain")
@Entity
public class Domain implements Serializable {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    //域名
    @Column(name = "domain", columnDefinition = "varchar(255) comment '域名'")
    private String domain;

    //状态
    @Column(name = "status", columnDefinition = "varchar(255) comment '状态'")
    private String status;

    //是否启用ssl
    @Column(name = "enable_ssl", columnDefinition = "TINYINT comment '是否启用ssl'")
    private boolean enableSsl;

    //访问量
    @Column(name = "visits", columnDefinition = "bigint comment '该域名的访问次数'")
    private long visits;

    // 证书过期时间
    @Column(name = "cert_exp_time", columnDefinition = "datetime comment '证书过期时间'")
    private LocalDateTime certExpTime;

    //添加时间
    @Column(name = "create_time", columnDefinition = "datetime comment '添加时间'")
    private LocalDateTime createTime;

    //备注信息
    @Column(name = "remark", columnDefinition = "varchar(1024) comment '备注信息'")
    private String remark;
}
