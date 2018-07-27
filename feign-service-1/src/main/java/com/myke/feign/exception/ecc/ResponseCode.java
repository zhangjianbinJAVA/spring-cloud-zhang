package com.myke.feign.exception.ecc;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/8/11 19:27
 */
public enum ResponseCode {
    EXCEPTION_CODE("99999-0", "系统异常，请稍后重试"),

    REMOTE_CALL_SERVICE_CODE("99999-1", "远程调用服务端异常"),

    NOT_VALID_ARGUMENT_CODE("99999-2", "参数校验异常"),

    PARAMETER_CONVERSION_ERROR_CODE("99999-3", "参数类型转换异常"),

    MISSING_REQUESTPA_RAMETER_CODE("99999-4", "缺少参数异常"),

    REMOTE_CALL_CLIENT_CODE("99999-5", "远程调用客户端异常"),

    REMOTE_CALL_HYSTRIXRUNTIME_CODE("99999-6", "远程调用运行时异常"),

    SPRING_MVC_HTTPMESSAGE_CONVERSION_CODE("99999-7", "参数序列化异常"),

    UNKNOWN_CODE("99999-100", "未知的异常");

    private String code;

    private String msg;

    ResponseCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
