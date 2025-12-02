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
public class Flag {

    private String emoji;
    @JSONField(name = "emoji_unicode")
    private String emojiUnicode;
    private String emojitwo;
    private String noto;
    private String twemoji;
    private String wikimedia;

}