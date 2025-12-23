package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 删除站点自定义替换内容</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-23 03:20
 **/
@Data
public class DeleteCustomContent {

    @NotBlank(message = "不允许空")
    @Min(value = 1)
    Integer id;
}
