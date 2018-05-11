package com.myke.ribbon.hystrix.client;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 1
 *
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/8/4 10:21
 * <p>
 * 手动方式
 * <p>
 * 创建请求命令
 * <p>
 * HystrixCommand 它用来封装具体的依赖服务调用逻辑
 * <p>
 * 通过继承的方式来实现
 */
public class HelloHystrixCommandManualCreateEnableCache extends HystrixCommand<String> {

    private static final String HELLO_SERVICE_URL = "http://localhost:18081/hystrix/ribbon";
    private static HystrixCommandGroupKey groupName = HystrixCommandGroupKey.Factory.asKey("GroupName");
    private static HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("hello");


    private Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;

    private String str;

    public HelloHystrixCommandManualCreateEnableCache(Setter setter, RestTemplate restTemplate, String str) {
        super(setter);

        // withGroupKey 来设置命令组名
        // andCommandKey 来设置命令名
        // andThreadPoolKey 实现更细粒度的 线程池 划分
//        super(Setter.withGroupKey(
//                HystrixCommandGroupKey.Factory.asKey("groupName")).
//                andCommandKey(HystrixCommandKey.Factory.asKey("commandName"))
//                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("threadPoolKey")));
        this.restTemplate = restTemplate;
        this.str = str;

        //初始化请求上下文
        HystrixRequestContext.initializeContext();
    }


    /**
     * 请求的同步执行
     *
     * @return
     * @throws Exception
     */
    @Override
    protected String run() throws Exception {
        //假设 ： 写操作
        String result = restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();

        // 刷新缓存， 清理缓存中失效的 str
        flushCache(str);

        return result;
    }


    /**
     * 开启缓存功能
     * <p>
     * 当不同的外部请求处理逻辑调用了同一个依赖服务时，Hystrix会根据 getCacheKey方法返回的值来区分
     * <p>
     * 是否是重复的请求，如果它们的cacheKey相同，那么该依赖服务只会在第一个请求到达时
     * <p>
     * 被真实地调用一次，另外一个请求则是直接从请求缓存中返回结果.
     *
     * @return
     */
    @Override
    protected String getCacheKey() {
        // 根据 str 的内容来 缓存请求数据
        return this.str;
    }

    /**
     * 刷新缓存，根据 str 进行清理
     *
     * @param str
     */
    public static void flushCache(String str) {
        HystrixRequestCache.getInstance(commandKey, HystrixConcurrencyStrategyDefault.getInstance())
                .clear(str);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);


        Setter set = Setter.withGroupKey(groupName).andCommandKey(commandKey);

        //请求的同步执行
        String helloResul = new HelloHystrixCommandManualCreateEnableCache(set, restTemplate, "hello").execute();
        System.out.println("请求的同步执行 结果：" + helloResul);

        //异步执行
        Future<String> hm = new HelloHystrixCommandManualCreateEnableCache(set, restTemplate, "hello").queue();
        // 异步结果
        String result = hm.get();
        System.out.println("异步执行 结果：" + result);
    }

}
