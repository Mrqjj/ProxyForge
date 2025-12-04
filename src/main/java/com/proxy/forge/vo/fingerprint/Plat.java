/**
  * Copyright 2025 bejson.com 
  */
package com.proxy.forge.vo.fingerprint;
import lombok.Data;

import java.util.List;


@Data
public class Plat {

    private String userAgent;
    private String platform;
    private String appCodeName;
    private Battery battery;
    private boolean webdriver;
    private String dnt;
    private String href;
    private String referrer;
    private String flashVersion;
    private List<Plugins> plugins;

}