package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 保存域名接口验证类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-26 19:47
 **/
@Data
public class SaveDomain {


    @NotBlank(message = "不允许null")
    private String domain;

    @NotBlank(message = "不允许null")
    private String status;

    private boolean ssl;


    private String remark;
}
