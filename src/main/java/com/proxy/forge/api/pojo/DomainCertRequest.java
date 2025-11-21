package com.proxy.forge.api.pojo;

import com.proxy.forge.tools.CertificateManagement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 域名申请字段</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-20 21:50
 **/
@Data
public class DomainCertRequest {

    // 环境ID
    @NotBlank(message = "不允许空或null")
//    @Pattern(
//            regexp = "^(?:\\*\\.)?(?=.{1,253}$)(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-))*\\.?$",
//            message = "域名格式不合法"
//    )
    private String domain;

    //调用凭证
    @NotNull(message = "不允许null")
    private String token;

    @NotBlank
    @Pattern(regexp = "HTTP|DNS", message = "只支持 HTTP 或 DNS")
    private String method;

}
