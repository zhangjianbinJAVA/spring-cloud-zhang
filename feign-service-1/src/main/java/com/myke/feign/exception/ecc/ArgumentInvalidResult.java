package com.myke.feign.exception.ecc;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/8/11 19:22
 * 
 * 
 *       参数无效时，返回的异常数据结构
 * 
 */
public class ArgumentInvalidResult {
    private String field;

    private Object rejectedValue;

    private String defaultMessage;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String toString() {
        return "ArgumentInvalidResult{" + "field='" + field + '\'' + ", rejectedValue=" + rejectedValue
                + ", defaultMessage='" + defaultMessage + '\'' + '}';
    }
}
