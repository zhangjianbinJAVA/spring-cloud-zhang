package com.example.zuul.exception.ecc;

import lombok.Data;

/**
 * @description:
 * @author: lilaien
 * @requireNo:
 * @createdate: 2017-01-09 16:20
 * @lastdate:
 */
@Data
public class CustomZuulException extends RuntimeException {
    protected String code;

    protected String msg;

    protected String detailMsg;

    protected String serviceId;

    protected String requestUrl;
    protected String traceId;


    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public CustomZuulException() {
    }

    public CustomZuulException(String code, String message, String detailMsg, String requestUrl, String traceId, String serviceId) {
        super(message);
        this.code = code;
        this.msg = message;
        this.detailMsg = detailMsg;
        this.requestUrl = requestUrl;
        this.traceId = traceId;
    }


    public CustomZuulException(String code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }

    public CustomZuulException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}
