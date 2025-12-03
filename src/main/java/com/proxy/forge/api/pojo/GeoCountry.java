package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.OffsetDateTime;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-03 21:14
 **/
@Data
public class GeoCountry {

    @NotNull
    OffsetDateTime startTime;
    @NotNull
    OffsetDateTime endTime;
}
