package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 用户登录描述类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-26 15:26
 **/
@Data
public class UserLogin {


    //调用凭证
    @NotBlank(message = "不允许null")
    private String userName;

    @NotBlank(message = "不允许null")
    private String passWord;
}
