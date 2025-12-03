package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 检查终端设备类型信息</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-26 21:39
 **/
@Data
public class CheckDeviceInfo {


    @NotBlank(message = "not allowed to be null")
    private String encStr;

    
}
