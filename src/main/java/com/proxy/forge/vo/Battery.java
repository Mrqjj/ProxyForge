/**
  * Copyright 2025 bejson.com 
  */
package com.proxy.forge.vo;

import lombok.Data;


@Data
public class Battery {

    private int level;
    private boolean charging;
    private int chargingTime;
    private String dischargingTime;
    private int d;
}