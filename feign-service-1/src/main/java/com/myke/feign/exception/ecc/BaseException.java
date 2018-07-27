package com.myke.feign.exception.ecc;

/**
 * @description:
 * @author: lilaien
 * @requireNo:
 * @createdate: 2017-01-09 16:20
 * @lastdate:
 */
public class BaseException extends RuntimeException {
    protected String code;

    protected String msg;

    public BaseException() {
    }

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }

    public BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
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
        return "BaseException{" + "code='" + code + '\'' + ", msg='" + msg + '\'' + '}';
    }
}
