package com.myke.ribbon.hystrix.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * 6
 *
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
public class HelloHystrixCommandAnnotationCreateObservable {

    private static final String HELLO_SERVICE_URL = "http://HELLO-SERVICE-1/hystrix/ribbon";

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Resource(name = "hystrixRestTemplate")
    private RestTemplate restTemplate;


    /**
     * EAGER 是该参数的模式值，表示使用observe()执行方式
     * <p>
     * observe 表示 Hot Observable， 订阅者只能看到整个操作的局部过程
     *
     * @param str
     * @return
     */
    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER)
    public String getHelloStr_observe(String str) {
        logger.info(" observe 执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }


    /**
     * LAZY 表示使用toObservable()执行方式
     * <p>
     * toObservable 表示：Cold Observable， 订阅者从一开始看到了整个操作的全部过程
     *
     * @param str
     * @return
     */
    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.LAZY)
    public String getHelloStr_toObservable(String str) {
        logger.info("toObservable 执行 getHelloStr_2 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }


}