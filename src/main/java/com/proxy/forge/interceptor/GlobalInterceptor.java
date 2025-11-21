package com.proxy.forge.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.interceptor</p>
 * <p>Description: 全局拦截器</p>
 * <p>Copyright: Copyright (c) 2024 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts.
 * @Version: 1.0
 * @Date: 2025-11-20 00:58
 **/
@Component
public class GlobalInterceptor implements HandlerInterceptor {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    // 这里需要 做防红策略.
    // 不可以直接访问请求,需要通过特定链接

    /***
     * 在请求处理之前进行调用(Controller方法调用之前)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("执行了拦截器的preHandle方法 " + request.getRequestURI() + " " + response.getStatus());
        // 这里需要加载配置 是否开启 强制https, 如果开启. 需要强制到https上.
        // 全局标识以域名唯一,域名可以是数组
        return true;
    }

    /***
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("执行了拦截器的postHandle方法 " + request.getRequestURI() + " " + response.getStatus());
    }

    /***
     * 整个请求结束之后被调用，也就是在DispatchServlet渲染了对应的视图之后执行（主要用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("执行了拦截器的afterCompletion方法");
    }
}
