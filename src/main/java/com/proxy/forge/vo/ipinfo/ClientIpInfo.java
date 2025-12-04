/**
 * Copyright 2025 lzltool.com
 */

package com.proxy.forge.vo.ipinfo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * Auto-generated: 2025-12-02 00:09:59
 *
 * @author lzltool.com
 * @website https://www.lzltool.com/JsonToJava
 */
@Data
public class ClientIpInfo {

    private String ip;
    private String type;
    private Carrier carrier;
    private Company company;
    private Connection connection;
    private Currency currency;
    private Location location;
    private Security security;
    @JSONField(name = "time_zone")
    private TimeZone timeZone;
}