package com.proxy.forge.service.impl;

import com.proxy.forge.service.ProxyRouterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 代理路由实现类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-20 01:59
 **/
@Service
public class ProxyRouterServiceImpl implements ProxyRouterService {


    @Override
    public ResponseEntity<?> dispatch(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }


}
