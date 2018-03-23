package com.myke.feign.web;

import com.myke.feign.client.HelloFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 14:35
 */
@RestController
public class HelloFeignController {

    @Autowired
    private HelloFeignClient helloFeignClient;

    @GetMapping("/hello")
    public String index() {
        return helloFeignClient.index();
    }
}
