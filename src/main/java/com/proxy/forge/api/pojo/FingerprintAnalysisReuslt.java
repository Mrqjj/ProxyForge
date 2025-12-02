package com.proxy.forge.api.pojo;

import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 指纹分析结果，客户IP分析结果</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-28 17:28
 **/
@Data
public class FingerprintAnalysisReuslt {

    private boolean result;

    private String message;
}
