package com.myke.ribbon.hystrix.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/8/4 10:21
 * <p>
 * 注解方式
 * <p>
 * 创建请求命令
 * <p>
 * HystrixCommand 它用来封装具体的依赖服务调用逻辑
 */
@Service
public class HelloHystrixCommandAnnotationFallBack {

    private static final String HELLO_SERVICE_URL = "http://HELLO-SERVICE-1/hystrix/ribbon";

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Resource(name = "hystrixRestTemplate")
    private RestTemplate restTemplate;


    /**
     * 通过断路器实现的服务回调逻辑
     * <p>
     * 除了通过断开具体的服务实例来模拟某个节点无法访问的情况之外，
     * 我们还可以模拟一下服务阻塞（长时间未响应）的情况
     *
     * @param str
     * @return
     */
    @HystrixCommand(fallbackMethod = "helloFallback")
    public String getHelloStr_1(String str) {
        logger.info("执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }

    /**
     * 服务消费者因调用的服务超时从而触发熔断请求， 并调用回调逻辑返回结果。
     * <p>
     * 模拟一下服务阻塞（长时间未响应）的情况
     *
     * @param str
     * @return
     */
    @HystrixCommand(fallbackMethod = "helloFallback")
    public String getHelloStr_2(String str) {
        logger.info("执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello-time-out?str={1}", String.class, str).getBody();
    }

    /**
     * 降级方法 必须 和 原方法 的参数个数 类型 一致，否则报错
     * <p>
     * com.netflix.hystrix.contrib.javanica.exception.FallbackDefinitionException: fallback method wasn't found
     *
     * @param str
     * @return
     */
    public String helloFallback(String str) {
        return "fallback error" + str;
    }

}