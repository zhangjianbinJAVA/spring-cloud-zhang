package com.myke.ribbon;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 15:08
 */
@EnableCircuitBreaker //开启断路器
@EnableSwagger2Doc
@EnableDiscoveryClient
@SpringBootApplication
//@SpringCloudApplication
public class RibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(RibbonApplication.class);
    }

}
