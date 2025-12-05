package com.proxy.forge.service.impl;

import com.proxy.forge.service.WhiteListService;
import com.proxy.forge.vo.WhiteListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class WhiteListServiceImpl implements WhiteListService {

    private static final String PREFIX = "whitelist:ip:";

    @Autowired
    private StringRedisTemplate redis;

    /**
     * 添加白名单
     */
    @Override
    public void addIp(String ip, Integer expireSeconds, String nodes) {
        String key = PREFIX + ip;
        redis.opsForValue().set(key, nodes);

        // 设置过期时间
        if (expireSeconds != null && expireSeconds > 0) {
            redis.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 查询所有白名单
     */
    @Override
    public List<WhiteListVO> list() {
        Set<String> keys = redis.keys(PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        List<WhiteListVO> list = new ArrayList<>();

        for (String key : keys) {
            String ip = key.replace(PREFIX, "");
            Long ttl = redis.getExpire(key);

            list.add(new WhiteListVO(ip, ttl != null ? ttl : -1, redis.opsForValue().get(key)));
        }

        return list;
    }

    /**
     * 删除 IP
     */
    @Override
    public void removeIp(String ip) {
        redis.delete(PREFIX + ip);
    }

    /**
     * 是否存在白名单中.
     *
     * @param ip
     * @return
     */
    @Override
    public boolean isExistsWhiteList(String ip) {
        return redis.opsForValue().get(PREFIX + ip) == null;
    }
}