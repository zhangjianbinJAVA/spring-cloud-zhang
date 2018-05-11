package com.myke.ribbon.hystrix.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * 2
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
public class HelloHystrixCommandAnnotationCreate {

    private static final String HELLO_SERVICE_URL = "http://HELLO-SERVICE-1/hystrix/ribbon";

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Resource(name = "hystrixRestTemplate")
    private RestTemplate restTemplate;


    /**
     * 同步执行的实现
     *
     * @param str
     * @return
     */
    @HystrixCommand
    public String getHelloStr_Synchronized(String str) {
        logger.info(" 同步执行的实现 执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }


    /**
     * 异步执行的实现
     *
     * @param str
     * @return
     */
    @HystrixCommand
    public Future<String> getHelloStr_async(String str) {
        logger.info("异步执行的实现 执行 getHelloStr_2 ……");
        return new AsyncResult<String>() {
            @Override
            public String invoke() {
                return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
            }
        };
    }


    /**
     * 设置命令名称、分组以及线程池划分
     *
     * commandKey 命令名称
     *
     * groupKey 分组名
     *
     * threadPoolKey 线程池划分名
     *
     * @param str
     * @return
     */
    @HystrixCommand(commandKey = "getHelloStr_Thread", groupKey = "groupKey", threadPoolKey = "threadKey")
    public String getHelloStr_Thread(String str) {
        logger.info(" 同步执行的实现 执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }

}