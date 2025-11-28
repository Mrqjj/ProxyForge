package com.proxy.forge.vo;

import lombok.Data;

@Data
public class Timing {

    private long connectStart;
    private long secureConnectionStart;
    private int unloadEventEnd;
    private long domainLookupStart;
    private long domainLookupEnd;
    private long responseStart;
    private long connectEnd;
    private long responseEnd;
    private long requestStart;
    private long domLoading;
    private int redirectStart;
    private long loadEventEnd;
    private long domComplete;
    private long navigationStart;
    private long loadEventStart;
    private long domContentLoadedEventEnd;
    private int unloadEventStart;
    private int redirectEnd;
    private long domInteractive;
    private long fetchStart;
    private long domContentLoadedEventStart;
}