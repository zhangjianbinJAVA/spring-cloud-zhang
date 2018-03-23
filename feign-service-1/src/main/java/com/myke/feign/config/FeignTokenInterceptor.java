package com.myke.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign 拦截器，用于添加自定义请求头
 *
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/5/24 12:59
 */
public class FeignTokenInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 1.模拟获取request.header参数
        //template.header(header, headerValue);
    }
}
