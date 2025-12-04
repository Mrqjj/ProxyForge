/**
  * Copyright 2025 bejson.com 
  */
package com.proxy.forge.vo.fingerprint;

import lombok.Data;


@Data
public class Capabilities {

    private boolean webgl;
    private boolean webgl2;
    private boolean serviceWorker;
    private boolean notifications;
    private boolean geolocation;
    private boolean localStorage;
    private boolean sessionStorage;
    private boolean indexedDB;
    private boolean canvas;
}