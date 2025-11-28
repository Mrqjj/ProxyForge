/**
  * Copyright 2025 bejson.com 
  */
package com.proxy.forge.vo;


import lombok.Data;

@Data
public class Performance {

    private long totalJSHeapSize;
    private long usedJSHeapSize;
    private long jsHeapSizeLimit;

}