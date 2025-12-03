package com.proxy.forge.controller;

import com.alibaba.fastjson2.JSONObject;
import com.proxy.forge.tools.GlobalStaticVariable;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>ProjectName: wechat</p>
 * <p>PackageName: com.wechat.controller</p>
 * <p>Description: 微信消息websocket控制器同步消息到客户端</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: 85773
 * @Version: 1.0
 * @Date: 2024-05-09 16:31
 **/
@Slf4j
@Component
@ServerEndpoint(value = "/wsLogs/{key}")
public class WebSocketController {

    /**
     * key → Set<sessionId>
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> KEY_SESSION_MAP = new ConcurrentHashMap<>();
    /**
     * sessionId → session
     */
    private static final ConcurrentHashMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();
    /**
     * key → 是否有线程在跑
     */
    private static final ConcurrentHashMap<String, AtomicBoolean> KEY_RUNNING = new ConcurrentHashMap<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // WebSocket 是多例，所以要用静态注入
    private static StringRedisTemplate redisStatic;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        WebSocketController.redisStatic = redisTemplate;
    }

// ------------------------ Redis 消费线程逻辑 ------------------------

    /**
     * 每个 key 只启动一个线程
     */
    private void startConsumerIfNeeded(String key) {
        KEY_RUNNING.putIfAbsent(key, new AtomicBoolean(false));

        AtomicBoolean runningFlag = KEY_RUNNING.get(key);

        if (runningFlag.compareAndSet(false, true)) {
            log.info("[启动 Redis 消费线程] key={}", key);
            startConsumerThread(key, runningFlag);
        }
    }

    /**
     * 根据 key 拉取 Redis 队列并广播
     */
    private void startConsumerThread(String key, AtomicBoolean flag) {
        new Thread(() -> {
            try {
                while (true) {
                    // 如果该 key 的在线人数 = 0 → 停止线程
                    if (!KEY_SESSION_MAP.containsKey(key) || KEY_SESSION_MAP.get(key).isEmpty()) {
                        log.info("[停止线程] key={} (无用户在线)", key);
                        break;
                    }
                    String redisKey = GlobalStaticVariable.REDIS_WEBSITE_LOGS_KEY + key;
                    String msg = redisStatic.opsForList()
                            .rightPop(redisKey, 5, TimeUnit.SECONDS);
                    if (StringUtils.isBlank(msg)) {
                        sleep(2000);
                        continue;
                    }
                    // 广播给该 key 的所有连接
                    for (String sid : KEY_SESSION_MAP.getOrDefault(key, new CopyOnWriteArraySet<>())) {
                        Session session = SESSION_MAP.get(sid);
                        if (session != null && session.isOpen()) {
                            try {
                                session.getAsyncRemote().sendText(msg);
                            } catch (Exception e) {
                                log.warn("发送失败 key={} sessionId={}", key, sid, e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("[线程异常退出] key={}", key, e);
            } finally {
                // 重置线程状态
                flag.set(false);
                log.info("[Redis 消费线程已退出] key={}", key);
            }

        }, "redis-consumer-" + key).start();
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("key") String key) {
        SESSION_MAP.put(session.getId(), session);
        KEY_SESSION_MAP.computeIfAbsent(key, k -> new CopyOnWriteArraySet<>()).add(session.getId());
        log.info("[WebSocket 连接] key={} session={}", key, session.getId());
        startConsumerIfNeeded(key);
    }

    /**
     * 用户断线
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session, @PathParam("key") String key) {
        log.info("[WebSocket 断开] key={} session={}", key, session.getId());
        SESSION_MAP.remove(session.getId());
        Set<String> sessions = KEY_SESSION_MAP.get(key);
        if (sessions != null) {
            sessions.remove(session.getId());
            // 该 key 当前无用户 → 自动停止线程
            if (sessions.isEmpty()) {
                KEY_SESSION_MAP.remove(key);
            }
        }
    }


    /**
     * 当接收到用户上传的消息
     *
     * @param session
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("[收到客户端消息] session={} msg={}", session.getId(), message);

        try {
            JSONObject obj = JSONObject.parse(message);
            if ("ping".equalsIgnoreCase(obj.getString("action"))) {
                JSONObject pong = new JSONObject();
                pong.put("action", "pong");
                session.getAsyncRemote().sendText(pong.toJSONString());
            }
        } catch (Exception ignored) {
        }
    }


    /**
     * 处理用户活连接异常
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket 错误 session={}", session.getId(), throwable);
        try {
            session.close();
        } catch (IOException ignored) {
        }
    }

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
