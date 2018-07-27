package com.myke.feign.config;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/5/23 21:08
 */
@Configuration
public class FeignLogConfiguration {
    /**
     * feign logger 日志
     *
     * none ：不记录任何信息
     * basic ：仅记录请求方法、URL以及响应状态码和执行时间
     * headres : 除了记录BASIC级别的信息之外，还会记录请求和响应的头信息
     * full :记 录所有请求与响应的明细，包括头信息、请求体、元数据等
     *
     * @return
     */
    @Bean
    @Primary
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * feign 关闭重试
     *
     * @return
     */
//    @Bean
//    public Retryer retryer() {
//        // 该 FeignClient 的重试次数，重试间隔为 ms，最大重试时间 ms,重试次数(包含第一次请求)。
//        // feign 请求处理超时 时就会进行重试，会有 RETRYING 再试 的日志
//        return new Retryer.Default(100, 2000, 4);
//    }

    @Bean
    public Retryer retryer(){
        return Retryer.NEVER_RETRY;
    }


    /**
     * feign取消重试
     *
     * @return
     */
//    @Bean
//    public Retryer feignRetryer() {
//        return Retryer.NEVER_RETRY;
//    }

    /**
     * feign 连接时间超时 和 读取超时
     *
     * @return
     */
//    @Bean
//    public feign.Request.Options feignRequestOption() {
//        // 请求连接超时时间，请求处理超时时间
//        return new feign.Request.Options(2000, 2000);
//    }

    /**
     * feign 拦截器
     *
     * @return
     */
//    @Bean
//    public FeignTokenInterceptor basicAuthRequestInterceptor() {
//        return new FeignTokenInterceptor();
//    }
}
