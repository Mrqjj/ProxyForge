/**
  * Copyright 2025 bejson.com 
  */
package com.proxy.forge.vo.fingerprint;
import lombok.Data;

import java.util.List;



@Data
public class ClientFingerprint {

    private Webgl webgl;
    private Plat plat;
    private Screen screen;
    private Performance performance;
    private Perf perf;
    private int timeZone;
    private Math math;
    private Capabilities capabilities;
    private Capabilities2 capabilities2;
    private Scripts scripts;
    private History history;
    private Automation automation;
    private List<Gyroscope> gyroscope;

}