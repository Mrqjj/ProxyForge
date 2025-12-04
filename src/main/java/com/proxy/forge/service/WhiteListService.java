package com.proxy.forge.service;
import com.proxy.forge.vo.WhiteListVO;
import org.springframework.stereotype.Service;
import java.util.List;

public interface WhiteListService {

    /**
     * 添加白名单
     */
    void addIp(String ip, Integer expireSeconds, String nodes);

    /**
     * 查询所有白名单
     */
    List<WhiteListVO> list();

    void removeIp(String ip);

    boolean isExistsWhiteList(String ip);
}