package com.proxy.forge.vo.fingerprint;

import lombok.Data;

@Data
public class Timing {

    private Long connectStart;
    private Long secureConnectionStart;
    private Long unloadEventEnd;
    private Long domainLookupStart;
    private Long domainLookupEnd;
    private Long responseStart;
    private Long connectEnd;
    private Long responseEnd;
    private Long requestStart;
    private Long domLoading;
    private Long redirectStart;
    private Long loadEventEnd;
    private Long domComplete;
    private Long navigationStart;
    private Long loadEventStart;
    private Long domContentLoadedEventEnd;
    private Long unloadEventStart;
    private Long redirectEnd;
    private Long domInteractive;
    private Long fetchStart;
    private Long domContentLoadedEventStart;
}