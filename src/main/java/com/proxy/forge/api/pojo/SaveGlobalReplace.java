package com.proxy.forge.api.pojo;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 添加全局替换</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 16:42
 **/
@Data
public class SaveGlobalReplace {

    // request.getRequestURI()
    @NotBlank(message = "不允许null 或 空")
    private String urlPattern;
    // response content-type
    @NotBlank(message = "不允许null 或 空")
    private String contentType;
    // response.body
    @NotBlank(message = "不允许null 或 空")
    private String responseContent;
}
