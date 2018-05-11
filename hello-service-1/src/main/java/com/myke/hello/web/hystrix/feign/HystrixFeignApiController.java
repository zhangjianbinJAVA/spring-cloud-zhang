package com.myke.hello.web.hystrix.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class HystrixFeignApiController {

    @Autowired
    private DiscoveryClient client;

    private AtomicInteger count = new AtomicInteger();


    @GetMapping("/api/timeout")
    public Map<String, String> timeout() {
        ServiceInstance instance = client.getLocalServiceInstance();
        log.info("/api/timeout count:{},host:{},port:{},serviceId:{}",
                count.getAndIncrement(), instance.getHost(), instance.getPort(), instance.getServiceId());

        //测试超时
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("zhang", "timeout");
        return map;
    }

}
