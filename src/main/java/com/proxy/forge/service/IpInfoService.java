package com.proxy.forge.service;

import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.vo.ipinfo.ClientIpInfo;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: ip信息接口类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-12-01 23:29
 **/
public interface IpInfoService {

    /**
     * 从Ipregistry.co获取指定IP地址的详细信息。
     *
     * @param ipAddress 待查询的IP地址字符串
     * @return 返回一个包含IP地址详细信息的ClientIpInfo对象。具体返回对象的结构取决于实现逻辑
     */
    ClientIpInfo getIpregistryCoDetails(String ipAddress);
}
