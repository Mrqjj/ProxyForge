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
public class Format {

	@JSONField(name ="decimal_separator")
	private String decimalSeparator;
	@JSONField(name ="group_separator")
	private String groupSeparator;
	private Negative negative;
	private Positive positive;

}