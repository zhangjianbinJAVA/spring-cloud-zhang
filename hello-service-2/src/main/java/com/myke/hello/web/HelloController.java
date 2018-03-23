package com.myke.hello.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 13:50
 */
@Slf4j
@RestController
public class HelloController {

    @Autowired
    private DiscoveryClient client;

    @GetMapping("/hello")
    public String index() {
        ServiceInstance instance = client.getLocalServiceInstance();
        log.info("/hello host:{},serviceId:{}",instance.getHost(), instance.getServiceId());
        return "Hello World";
    }
}
