package com.proxy.forge.vo;

import lombok.Data;

/**
 * Created by Administrator on 2018/3/26.
 *
 * @author QuJianJun
 */
@Data
public class ResponseApi {
    /**
     * 状态码
     */
    private int statusCode;
    /**
     * 提示信息
     */
    private String message;

    /**
     * 数据对象
     */
    private Object data;
    private int total;

    public ResponseApi(int statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ResponseApi(int statusCode, String message, Object data, int total) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.total = total;
    }
}
