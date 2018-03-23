package com.myke.feign.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 14:33
 */
@FeignClient("HELLO-SERVICE-1")
public interface HelloFeignClient {
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    String index();
}
