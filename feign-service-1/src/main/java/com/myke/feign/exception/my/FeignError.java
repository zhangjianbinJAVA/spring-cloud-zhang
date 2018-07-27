package com.myke.feign.exception.my;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * feign
 */
//@Component
public class FeignError implements ErrorDecoder {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Exception decode(String s, Response response) {
        Exception exception = null;
        // 这里只封装4开头的请求异常
        if (400 <= response.status() || response.status() < 500) {
            exception = new HystrixBadRequestException("request exception wrapper", exception);
        } else {
            logger.error(exception.getMessage(), exception);
        }
        return exception;
    }
}
