package com.proxy.forge.dto; // 使用你示例中的 POJO 包名

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 *
 * <p>ProjectName: MirrorAmazonProject (或你的项目名)</p>
 * <p>PackageName: com.vivcms.mirror.pojo</p>
 * <p>Description: API 密钥信息表 实体类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-01 22:00
 **/
@Entity
@Table(name = "api_keys")
@Data
public class ApiKeys {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", columnDefinition = "bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID'")
    private Long id;

    @Column(name = "provider", nullable = false, columnDefinition = "varchar(50) NOT NULL COMMENT 'API提供商名称'")
    private String provider;

    @Column(name = "api_key", nullable = false, columnDefinition = "varchar(255) NOT NULL COMMENT 'API密钥'")
    private String apiKey;

    @Column(name = "credits_remaining", columnDefinition = "bigint NULL DEFAULT NULL COMMENT '剩余点数'")
    private Long creditsRemaining;

    @Column(name = "initial_credits", columnDefinition = "bigint NULL DEFAULT NULL COMMENT 'Key的初始总次数'")
    private Long initialCredits;

    @Column(name = "is_active", nullable = false, columnDefinition = "tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否激活 (1=激活, 0=禁用)'")
    private Boolean isActive = true;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_used_at", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后使用时间'")
    private Date lastUsedAt;

    @Column(name = "notes", columnDefinition = "varchar(255) NULL DEFAULT NULL COMMENT '备注信息'")
    private String notes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    private Date createdAt;

}