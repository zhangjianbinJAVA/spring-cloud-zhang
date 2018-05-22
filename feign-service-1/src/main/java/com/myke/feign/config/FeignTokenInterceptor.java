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
        // 1.添加 feign 的请求头 参数
//        template.header(header, headerValue);
//          template.header("Accept-Encoding","gzip");

        // 对所有的请求url 追加请求参数
        template.query("token","ZGVsZXRlX3Rva2VuX2J5X2Jhc2U2NA==");
    }
}
