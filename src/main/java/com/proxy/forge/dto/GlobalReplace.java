package com.proxy.forge.dto;

import jakarta.persistence.*;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.dto</p>
 * <p>Description: 全局拦截uri</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 16:23
 **/
@Data
@Table
@Entity
public class GlobalReplace {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    // request.getRequestURI()
    @Column(name = "url_pattern", columnDefinition = "varchar(255) comment '匹配路径uri'")
    private String urlPattern;
    // response content-type
    @Column(name = "content_type", columnDefinition = "varchar(255) comment '响应类型 content-type'")
    private String contentType;
    // response.body
    @Column(name = "response_content", columnDefinition = "TEXT comment '响应内容'")
    private String responseContent;
}
