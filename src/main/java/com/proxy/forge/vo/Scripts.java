package com.proxy.forge.vo;
import lombok.Data;

import java.util.List;

@Data
public class Scripts {

    private List<String> dynamicUrls;
    private List<Integer> inlineHashes;
    private int elapsed;
    private int dynamicUrlCount;
    private int inlineHashesCount;

}