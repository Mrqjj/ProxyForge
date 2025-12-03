package com.proxy.forge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;




@Data
@AllArgsConstructor
public class GeoContryResultVO {
    private String country;
    private Long count;
}