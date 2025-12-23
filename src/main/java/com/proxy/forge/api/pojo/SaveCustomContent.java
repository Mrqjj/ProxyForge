package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 保存替换内容</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-23 03:00
 **/
@Data
public class SaveCustomContent {

    // id
    Integer id;
    @NotBlank(message = "不允许Null")
    String webSiteHost;
    @NotBlank(message = "不允许Null")
    String urlPath;
    @NotBlank(message = "不允许Null")
    Boolean download;
    @NotBlank(message = "不允许Null")
    String fileName;
    @NotBlank(message = "不允许Null")
    private String content;
    @NotBlank(message = "不允许Null")
    private String contentType;
    @NotBlank(message = "不允许Null")
    private Boolean status;
}
