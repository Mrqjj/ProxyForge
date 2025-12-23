package com.proxy.forge.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.dto</p>
 * <p>Description: web 站点自定义路径内容替换.</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-23 00:21
 **/
@Table(name = "web_site_replace")
@Data
@Entity
public class WebSiteReplace {

    // 数据ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    // 站点ID（编辑时有值）
    @Column(name = "web_site_host", columnDefinition = "varchar(1000) comment '站点域名.'")
    private String webSiteHost;

    // 根据路径进行替换  路径具有唯一性
    @Column(name = "url_path", columnDefinition = "varchar(1000) comment '请求路径'")
    private String urlPath;

    // 这是一个标志位,代表该条数据配置是否启用文件下载功能 true 为下载，false 为 返回content内容，如果为true content 存的内容为 文件路径
    @Column(name = "download", columnDefinition = "TINYINT comment '是否启用下载功能'")
    private Boolean download;

    @Column(name = "file_name", columnDefinition = "varchar(255) comment '返回下载的文件名'")
    private String fileName;

    // 响应内容...  这里可能是个文件下载， 要支持文件上传， 响应文件
    @Column(name = "content", columnDefinition = "text comment '响应内容.'")
    private String content;

    // 内容类型。 application/json;  text/html;  等等...
    @Column(name = "content_type", columnDefinition = "varchar(255) comment '响应类型'")
    private String contentType;

    // 状态 是否启用 这里是一个开关，添加数据的时候默认启用
    @Column(name = "status", columnDefinition = "TINYINT comment '是否启用'")
    private Boolean status;

    // 添加时间
    @Column(name = "add_time", columnDefinition = "datetime default CURRENT_TIMESTAMP comment '添加时间'")
    private LocalDateTime addTime;
}
