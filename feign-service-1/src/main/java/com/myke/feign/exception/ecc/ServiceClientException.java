package com.myke.feign.exception.ecc;

import com.netflix.hystrix.exception.HystrixBadRequestException;

import java.util.Map;

/**
 * Created by zhangzhonghua on 2017/1/11.
 */
public class ServiceClientException extends HystrixBadRequestException {

    private String code;

    private String msg;

    private Map<String, Object> map;

    public ServiceClientException(Map<String, Object> map) {
        super(map.get("message").toString());

        this.code = map.get("code") == null ? ResponseCode.UNKNOWN_CODE.getCode()
                : map.get("code").toString();
        this.msg = map.get("msg") == null ? ResponseCode.UNKNOWN_CODE.getMsg() : map.get("msg").toString();

        this.map = map;
    }

    public ServiceClientException(String message) {
        super(message);
    }

    public ServiceClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
