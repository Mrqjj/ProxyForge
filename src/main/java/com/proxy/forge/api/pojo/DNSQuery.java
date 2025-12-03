package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 查询dns 参数</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-28 16:14
 **/
@Data
public class DNSQuery {

    @NotBlank( message = "不允许为null")
    private String domain;
    @NotBlank( message = "不允许为null")
    private String type;
}
