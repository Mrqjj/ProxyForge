package com.proxy.forge.controller;

import com.proxy.forge.api.pojo.CheckDeviceInfo;
import com.proxy.forge.service.ProxyRouterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;


/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.controller</p>
 * <p>Description: 总控制器</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-19 22:57
 **/
@RestController
public class MasterController {

    @Autowired
    ProxyRouterService proxyRouterService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 处理所有入站请求并返回请求URI。
     *
     * @param request  包含客户端对 servlet 发出请求的 HttpServletRequest 对象。
     * @param response HttpServletResponse对象，接收用户输入并发送用户输出。
     * @return 一个ResponseEntity，主体设置为请求URI，表示HTTP响应成功。
     */
    @RequestMapping("/**")
    public Object proxyRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return proxyRouterService.dispatch(request, response);
    }


    @RequestMapping(value = "/check")
    public Object check(@RequestBody @Validated CheckDeviceInfo checkDeviceInfo, HttpServletRequest request, HttpServletResponse response) {
        return proxyRouterService.check(checkDeviceInfo, request, response);
    }

    @RequestMapping(value = "/sr")
    public Object startRequest(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok().body("sr");
    }

    /**
     * 处理ACME挑战域名验证请求。
     *
     * @param token    ACME服务器作为域验证过程一部分提供的令牌。
     * @param request  包含客户端对servlet请求的HttpServletRequest对象。
     * @param response 用于将响应返回客户端的 HttpServletResponse 对象。
     * @return 一个表示ACME挑战响应的对象，通常是简单的字符串或ResponseEntity。
     */
    @RequestMapping(value = "/.well-known/acme-challenge/{token}")
    public Object challenge(@PathVariable("token") String token, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok().body(stringRedisTemplate.opsForValue().get("certChalleng:" + token));
    }
}
