package com.myke.ribbon.web;


import com.myke.ribbon.hystrix.client.HelloHystrixCommandAnnotationFallBack;
import com.sun.org.apache.regexp.internal.RE;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 15:10
 */
@RestController
@RequestMapping("/hystrix/ribbon")
public class HelloHystrixRibbonController {

    @Autowired
    public HelloHystrixCommandAnnotationFallBack HelloHystrixCommandAnnotation;

    @Autowired
    private com.myke.ribbon.hystrix.client.HelloHystrixCommandAnnotationCreate helloHystrixCommandAnnotationCreate;

    @GetMapping("/hello-hystrix")
    public String helloHystrix() {
        return HelloHystrixCommandAnnotation.getHelloStr_1("zhang");
    }

    @GetMapping("/hello-hystrix-time-out")
    public String helloHystrixTimeOut() {
        return HelloHystrixCommandAnnotation.getHelloStr_2("zhang");
    }


    @GetMapping("/hello-hystrix-annotation-create-synchronized")
    public String helloHystrixAnnotationCreateSynchronized() {
        return helloHystrixCommandAnnotationCreate.getHelloStr_Synchronized("zhang");
    }

    @GetMapping("/hello-hystrix-annotation-create-async")
    public String helloHystrixAnnotationCreateAsync() {
        try {
            return helloHystrixCommandAnnotationCreate.getHelloStr_async("zhang").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "报错";
    }

}
