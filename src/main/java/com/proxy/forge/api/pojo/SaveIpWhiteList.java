package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 保存ip白名单</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-02 17:11
 **/
@Data
public class SaveIpWhiteList {

    @Pattern(
            regexp = "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}" +
                    "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$",
            message = "Invalid IPv4 address"
    )
    private String ip;

    private Integer ttl;

    private String notes;
}
