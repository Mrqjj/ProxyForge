package com.proxy.forge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatResultVO {
    private List<String> dates;
    private List<Long> pv;
    private List<Long> uv;
    private Long totalUv;
}