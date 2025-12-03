/**
 * Copyright 2025 lzltool.com
 */

package com.proxy.forge.vo.ipinfo;

import java.util.Date;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * Auto-generated: 2025-12-02 00:09:59
 *
 * @author lzltool.com
 * @website https://www.lzltool.com/JsonToJava
 */
@Data
public class TimeZone {

	private String id;
	private String abbreviation;
	@JSONField(name ="current_time")
	private Date currentTime;
	private String name;
	private int offset;
	@JSONField(name ="in_daylight_saving")
	private boolean inDaylightSaving;

}