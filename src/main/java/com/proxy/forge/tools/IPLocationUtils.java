package com.proxy.forge.tools;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;

import java.io.IOException;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.tools</p>
 * <p>Description: ip地理位置转换类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-03 15:44
 **/

public class IPLocationUtils {

    private static volatile IP2Location instance;
    // BIN 文件路径（你按需修改）
    private static final String DB_FILE = "./ip2location/IP2LOCATION-LITE-DB11.IPV6.BIN";
    private IPLocationUtils() {}

    /**
     * 单例获取实例（DCL）
     */
    public static IP2Location getInstance() {
        if (instance == null) {
            synchronized (IPLocationUtils.class) {
                if (instance == null) {
                    instance = new IP2Location();
                    try {
                        instance.Open(DB_FILE);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to load IP2Location DB", e);
                    }
                }
            }
        }
        return instance;
    }

    /**
     * 简化查询方法
     */
    public static IPResult query(String ip) {
        try {
            return getInstance().IPQuery(ip);
        } catch (Exception e) {
            try {
                return getInstance().IPQuery("0.0.0.0");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
