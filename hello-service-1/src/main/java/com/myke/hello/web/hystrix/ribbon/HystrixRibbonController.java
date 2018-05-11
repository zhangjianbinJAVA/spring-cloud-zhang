package com.myke.hello.web.hystrix.ribbon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/hystrix/ribbon")
public class HystrixRibbonController {

    private final static Logger LOG = LoggerFactory.getLogger(HystrixRibbonController.class);

    @Autowired
    private DiscoveryClient client;

    private AtomicInteger count = new AtomicInteger();

    @GetMapping("/hello")
    public String index(@RequestParam(required = false) String str) {
        ServiceInstance instance = client.getLocalServiceInstance();
        LOG.info("/hello count:{},host:{},port:{},serviceId:{}", count.getAndIncrement(), instance.getHost(), instance.getPort(), instance.getServiceId());
        return "Hello World" + str;
    }


    @GetMapping("/hello-id")
    public List<Long> hello_ids(@RequestParam List<Long> ids) {
        ServiceInstance instance = client.getLocalServiceInstance();
        LOG.info("/hello_ids count:{},host:{},port:{},serviceId:{}", count.getAndIncrement(), instance.getHost(), instance.getPort(), instance.getServiceId());
        LOG.info("批理处理 ：{}", ids);
        return ids;
    }

    @GetMapping("/hello-id/{id}")
    public Long hello_id(@PathVariable Long id) {
        ServiceInstance instance = client.getLocalServiceInstance();
        LOG.info("/hello_id count:{},host:{},port:{},serviceId:{}", count.getAndIncrement(), instance.getHost(), instance.getPort(), instance.getServiceId());
        LOG.info("单个处理 ：{}", id);
        return id;
    }

    @GetMapping("/hello-time-out")
    public String helloTimeOut(@RequestParam(required = false) String str) {
        ServiceInstance instance = client.getLocalServiceInstance();
        LOG.info("/hello-time-out count:{},port:{},serviceId:{}", instance.getHost(), instance.getPort(), instance.getServiceId());

        // 让处理线程等待几秒钟
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Hello World" + str;
    }

}
