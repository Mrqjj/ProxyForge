package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 添加apiKey</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-01 22:25
 **/
@Data
public class SaveApiKey {

    private Long id;
    // 初始额度
    private Long initialCredits;
    // 剩余 额度
    private Long creditsRemaining;

    @NotBlank(message = "nonnull")
    private String apiKey;

    @NotBlank(message = "nonnull")
    private String provider;

    private String notes;

    private Boolean isActive;
}
