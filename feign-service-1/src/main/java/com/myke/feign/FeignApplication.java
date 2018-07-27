package com.myke.feign;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 14:32
 */
//@EnableSwagger2Doc
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignApplication.class, args);
    }


//    @Bean
//    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
//                              SpringClientFactory clientFactory) {
//        return new CustomLoadBalancerFeignClient(new Client.Default(null, null),
//                cachingFactory, clientFactory);
//    }
}
