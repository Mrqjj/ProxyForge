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
public class Currency {

    private String code;
    private String name;
    @JSONField(name = "name_native")
    private String nameNative;
    private String plural;
    @JSONField(name = "plural_native")
    private String pluralNative;
    private String symbol;
    @JSONField(name = "symbol_native")
    private String symbolNative;
    private Format format;

}