package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 自定义响应内容配置</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-23 01:40
 **/

@Data
public class QueryCustomContent {


    @Min(value = 1)
    private int pageNum;
    @Min(value = 1)
    private int pageSize;
    @NotBlank(message = "not allowed to be null")
    private String webSiteHost;
}
