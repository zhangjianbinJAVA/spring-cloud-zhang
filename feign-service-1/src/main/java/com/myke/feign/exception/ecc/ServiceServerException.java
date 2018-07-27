package com.myke.feign.exception.ecc;

import java.util.Map;

/**
 * Created by zhangzhonghua on 2017/1/11.
 */
public class ServiceServerException extends BaseException {

    private Map<String, Object> map;

    public ServiceServerException(Map<String, Object> map) {
        super.code = map.get("code") == null ? ResponseCode.UNKNOWN_CODE.getCode()
                : map.get("code").toString();
        super.msg = map.get("msg") == null ? ResponseCode.UNKNOWN_CODE.getMsg() : map.get("msg").toString();

        map.put("remoteCode", map.get("code") == null ? ResponseCode.UNKNOWN_CODE.getCode()
                : map.get("code").toString());
        map.put("remoteMsg",map.get("msg") == null ? ResponseCode.UNKNOWN_CODE.getMsg() : map.get("msg").toString());
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
