package com.proxy.forge.vo;

import lombok.Data;

@Data
public class WhiteListVO {
    private String ip;
    private Long ttl;
    private String notes;

    public WhiteListVO(String ip, Long ttl,String notes) {
        this.ip = ip;
        this.ttl = ttl;
        this.notes = notes;
    }
}