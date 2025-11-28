package com.proxy.forge.api.pojo;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.api.pojo</p>
 * <p>Description: 搜索站点列表</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-28 13:07
 **/
@Data
public class SearchWebSite {


    private String keyWord;   // 搜索关键词（站点名称）
    @Min(value = 1)
    private Integer domainId;    // 域名筛选（可选，空表示全部）
    private String status;    // 状态筛选（running/stopped/maintenance，空表示全部）
    @Min(value = 1)
    private int pageNum;      // 页码，从1开始
    @Min(value = 1)
    private int pageSize;     // 每页数量
}
