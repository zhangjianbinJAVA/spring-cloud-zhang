package com.myke.hystrix;

import feign.Feign;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

//@EnableCircuitBreaker
//@Configuration
//public class DisableHystrixConfiguration {
//
//    /**
//     * 针对 某个服务客户端关闭 Hystrix 功能
//     *
//     * 某个客户端引用通过 configuration 配置项来引用这个配置
//     *
//     * @return
//     */
//    @Bean
//    @Scope("prototype")
//    public Feign.Builder feignBuilder() {
//        return Feign.builder();
//    }
//}
