package com.proxy.forge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class DomainStatVO {
    private String domain;
    private Long count;
}