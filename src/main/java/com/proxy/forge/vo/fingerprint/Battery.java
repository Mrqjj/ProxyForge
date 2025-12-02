/**
  * Copyright 2025 bejson.com 
  */
package com.proxy.forge.vo.fingerprint;


import lombok.Data;

@Data
public class Battery {

    private double level;
    private Boolean charging;
    private int chargingTime;
    private String dischargingTime;
    private int d;
}