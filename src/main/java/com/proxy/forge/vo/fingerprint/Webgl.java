package com.proxy.forge.vo.fingerprint;


import lombok.Data;

@Data
public class Webgl {

    private String renderer;
    private String vendor;
    private String glVersion;
    private String shadingLanguageVersion;
    private int maxTextureSize;
    private int maxCombinedTextureImageUnits;
    private int maxVertexAttribs;
}