package com.proxy.forge.tools;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.tools</p>
 * <p>Description: 全局静态变量提示信息</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-28 13:39
 **/
public class GlobalStaticVariable {

    /**
     * 用于存储和检索 Redis 中网站配置数据的密钥。
     * 该常数作为密钥的前缀，确保它们在 Redis 中唯一可识别。
     */
    public static final String REDIS_WEBSITE_CACHE_KEY = "webSiteConfig:";
    /**
     * 用于存储和检索 Redis 中网站日志数据的密钥。
     * 该常量作为密钥的前缀，确保它们在 Redis 中唯一可识别。
     */
    public static final String REDIS_WEBSITE_LOGS_KEY =  "webSiteLogs:";
    /**
     * 常量，表示API调用成功时的返回消息。
     * 该字符串用于在API响应中指示操作已成功完成。
     */
    public static final String API_MESSAGE_SUCCESS = "成功";
    /**
     * 常量，表示API调用失败时的返回消息。
     * 该字符串用于在API响应中指示操作未能成功完成。
     */
    public static final String API_MESSAGE_FAIL = "失败";
    /**
     * 常量，表示站点或目标网址存在。
     * 该字符串用于指示某个网站或指定的URL是存在的。
     */
    public static final String WEBSITE_OR_TARGET_URL_EXISTS = "站点或目标网址存在.";

    /**
     * 常量，表示站点详情不存在。
     * 该字符串用于指示在请求的上下文中没有找到特定网站的详细信息。
     */
    public static final String WEBSITE_DETAIL_NO_EXISTS = "站点详情不存在";
}
