package com.myke.ribbon.hystrix.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * 3
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
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello-time-out?str={1}", String.class, str).getBody();
    }


    /**
     * 使用 @HystrixCommand 中的 fallbackMethod 参数来指定具体的服务降级实现方法
     * <p>
     * 除了 HystrixBadRequestException之外，其他异常均会被 Hystrix 认为命令执行失败并触发服务降级的处理逻辑.
     * <p>
     * ignoreExceptions 忽略 指定异常类型，不触发 fallback 逻辑
     *
     * @param str
     * @return
     */
    @HystrixCommand(fallbackMethod = "helloFallback", ignoreExceptions = {BadRequestException.class})
    public String getHelloStr_ignoreExceptions(String str) {

        try {
            logger.info("执行 getHelloStr_1 ……");
            if (str.equals("my")) {
                throw new BadRequestException("不触发 fallback 逻辑");
            }

        } catch (Exception e) {
            throw e;
        }
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello-time-out?str={1}", String.class, str).getBody();
    }

    /**
     * 不触发 fallback 逻辑
     *
     * @param str
     * @return
     */
    @HystrixCommand(fallbackMethod = "helloFallback")
    public String getHelloStr_HystrixBadRequestException(String str) {

        try {
            logger.info("执行 getHelloStr_1 ……");
            if (str.equals("my")) {
                throw new HystrixBadRequestException("不触发 fallback 逻辑", new RuntimeException("不触发 fallback 逻辑"));
            }
        } catch (Exception e) {
            throw e;
        }
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
    public String helloFallback(String str, Throwable throwable) {
        logger.error("异常：" + throwable.getMessage(), throwable);
        return "fallback error" + str;
    }

}

class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}