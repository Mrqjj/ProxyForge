package com.proxy.forge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.vo</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-03 22:09
 **/
@Data
@AllArgsConstructor
public class GeoCityResultVO {
    private String country;
    private String province;
    private String city;
    private Long count;
}
