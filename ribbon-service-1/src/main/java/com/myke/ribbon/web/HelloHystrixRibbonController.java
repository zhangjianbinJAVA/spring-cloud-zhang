package com.myke.ribbon.web;


import com.myke.ribbon.hystrix.client.HelloHystrixCommandAnnotationCreate;
import com.myke.ribbon.hystrix.client.HelloHystrixCommandAnnotationCreateEnableCache;
import com.myke.ribbon.hystrix.client.HelloHystrixCommandAnnotationCreateObservable;
import com.myke.ribbon.hystrix.client.HelloHystrixCommandAnnotationFallBack;
import com.netflix.client.config.IClientConfig;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 15:10
 */
@RestController
@RequestMapping("/hystrix/ribbon")
public class HelloHystrixRibbonController {

    private Logger logger = LoggerFactory.getLogger(HelloHystrixRibbonController.class);

    @Autowired
    private SpringClientFactory springClientFactory;


    @Autowired
    public HelloHystrixCommandAnnotationFallBack helloHystrixCommandAnnotationFallBack;

    @Autowired
    private HelloHystrixCommandAnnotationCreate helloHystrixCommandAnnotationCreate;


    @Autowired
    private HelloHystrixCommandAnnotationCreateObservable helloHystrixCommandAnnotationCreateObservable;

    @Autowired
    private HelloHystrixCommandAnnotationCreateEnableCache helloHystrixCommandAnnotationCreateEnableCache;

    @GetMapping("/hello-hystrix")
    public String helloHystrix() {

        /**
         * 获取ribbon 配置信息
         */
        IClientConfig clientConfig = springClientFactory.getClientConfig("HELLO-SERVICE-1");

        logger.info("clientConfig:{}", clientConfig);

        return helloHystrixCommandAnnotationFallBack.getHelloStr_1("zhang");
    }

    @GetMapping("/hello-hystrix-time-out")
    public String helloHystrixTimeOut() {
        return helloHystrixCommandAnnotationFallBack.getHelloStr_2("zhang");
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

    @GetMapping("/hello-hystrix-annotation-create-observe")
    public String helloHystrixAnnotationCreateObserve() {
        return helloHystrixCommandAnnotationCreateObservable.getHelloStr_observe("zhang");
    }

    @GetMapping("/hello-hystrix-annotation-create-toObservable")
    public String helloHystrixAnnotationCreateToObservable() {
        return helloHystrixCommandAnnotationCreateObservable.getHelloStr_toObservable("zhang");
    }


    @GetMapping("/hello-hystrix-ignoreExceptions")
    public String helloHystrixIgnoreExceptions(@RequestParam String str) {
        return helloHystrixCommandAnnotationFallBack.getHelloStr_ignoreExceptions(str);
    }

    @GetMapping("/hello-hystrix-hystrixBadRequestException")
    public String helloHystrixHystrixBadRequestException(@RequestParam String str) {
        return helloHystrixCommandAnnotationFallBack.getHelloStr_HystrixBadRequestException(str);
    }


    @GetMapping("/hello-hystrix-enable-cache")
    public String helloHystrixEnableCache(@RequestParam String str) {
        HystrixRequestContext.initializeContext();
        //第一次发起请求
        String helloStr_cache = helloHystrixCommandAnnotationCreateEnableCache.getHelloStr_Cache(str);
        //参数和上次一致，使用缓存数据
        helloHystrixCommandAnnotationCreateEnableCache.getHelloStr_Cache(str);
        //参数不一致，发起新请求
        return helloHystrixCommandAnnotationCreateEnableCache.getHelloStr_Cache(str + "12");
    }

    @GetMapping("/hello-hystrix-cache-update")
    public String helloHystrixCacheUpdate(@RequestParam String str) {
        HystrixRequestContext.initializeContext();
        helloHystrixCommandAnnotationCreateEnableCache.getHelloStr_Update(str);
        return helloHystrixCommandAnnotationCreateEnableCache.getHelloStr_Update(str);
    }

    @GetMapping("/hello-hystrix-cacheKeyMethod")
    public String helloHystrixcacheKeyMethod(@RequestParam String str) {
        HystrixRequestContext.initializeContext();
        helloHystrixCommandAnnotationCreateEnableCache.getHelloStr_cacheKeyMethod(str);
        return helloHystrixCommandAnnotationCreateEnableCache.getHelloStr_cacheKeyMethod(str);
    }
}
