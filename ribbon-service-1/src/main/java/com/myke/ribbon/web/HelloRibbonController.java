package com.myke.ribbon.web;

import com.myke.ribbon.client.HelloRibbonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 15:10
 */
@RestController
public class HelloRibbonController {
    @Autowired
    private HelloRibbonClient helloRibbonClient;


    @GetMapping("/hello")
    public String index() {
        return helloRibbonClient.index();
    }

    @GetMapping("/retry-hello")
    public String retryHello() {
        return helloRibbonClient.retryHello();
    }

}
