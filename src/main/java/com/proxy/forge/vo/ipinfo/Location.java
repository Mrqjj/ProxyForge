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
public class Location {

	private Continent continent;
	private Country country;
	private Region region;
	private String city;
	private String postal;
	private double latitude;
	private double longitude;
	private Language language;
	@JSONField(name ="in_eu")
	private boolean inEu;

}