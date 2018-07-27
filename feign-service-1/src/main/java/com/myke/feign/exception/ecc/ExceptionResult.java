package com.myke.feign.exception.ecc;

/**
 * Created by zhangjianbin on 2017/1/22.
 */
public class ExceptionResult {
    private String code;

    private String msg;

    public ExceptionResult() {
    }

    public ExceptionResult(String code, String message) {

        this.code = code;
        this.msg = message;
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

    @Override
    public String toString() {
        return "ExceptionResult{" + "code='" + code + '\'' + ", msg='" + msg + '\'' + '}';
    }
}
