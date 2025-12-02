/**
 * Copyright 2025 lzltool.com
 */

package com.proxy.forge.vo.ipinfo;
import java.util.List;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * Auto-generated: 2025-12-02 00:09:59
 *
 * @author lzltool.com
 * @website https://www.lzltool.com/JsonToJava
 */
@Data
public class Country {

    private int area;
    private List<String> borders;
    @JSONField(name = "calling_code")
    private String callingCode;
    private String capital;
    private String code;
    private String name;
    private int population;
    @JSONField(name = "population_density")
    private double populationDensity;
    private Flag flag;
    private List<Languages> languages;
    private String tld;
}