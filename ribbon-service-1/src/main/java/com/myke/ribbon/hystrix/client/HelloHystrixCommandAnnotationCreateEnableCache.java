package com.myke.ribbon.hystrix.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * 7
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
public class HelloHystrixCommandAnnotationCreateEnableCache {

    private static final String HELLO_SERVICE_URL = "http://HELLO-SERVICE-1/hystrix/ribbon";

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Resource(name = "hystrixRestTemplate")
    private RestTemplate restTemplate;


    /**
     * 缓存清理
     * <p>
     * update类型的操作上对失效的缓存进行清理
     * <p>
     * commandKey属性是必须要指定的, 它用来指明需要使用请求缓存的请求命令
     *
     * @param str
     * @return
     */
    @CacheRemove(commandKey = "getHelloStr_Cache")
    @HystrixCommand
    public String getHelloStr_Update(String str) {
        logger.info(" 同步执行的实现 执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }

    /**
     * CacheResult 开启请求缓存 Hystrix会将该结果置入请求缓存中，
     * 而它的缓存 Key值 会使用所有的参数
     *
     * @param str
     * @return
     */
    @CacheResult
    @HystrixCommand
    public String getHelloStr_Cache(String str) {
        logger.info(" 同步执行的实现 执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }

    /**
     * 指定具体的缓存Key生成规则
     * <p>
     * cacheKeyMethod 方法来指定具体的生成函数
     *
     * @param str
     * @return
     */
    @CacheResult(cacheKeyMethod = "getHelloStr_cacheKeyMethod_str")
    @HystrixCommand
    public String getHelloStr_cacheKeyMethod(String str) {
        logger.info(" 同步执行的实现 执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }

    /**
     * 缓存 key 生成 规则 方法
     *
     * @param str
     * @return
     */
    private String getHelloStr_cacheKeyMethod_str(String str) {
        logger.info("缓存 key 生成 规则 方法 使用");
        return str;
    }


}