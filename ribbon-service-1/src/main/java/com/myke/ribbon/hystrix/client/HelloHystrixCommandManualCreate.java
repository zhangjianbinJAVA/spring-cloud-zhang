package com.myke.ribbon.hystrix.client;

import com.netflix.hystrix.*;
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
public class HelloHystrixCommandManualCreate extends HystrixCommand<String> {

    private static final String HELLO_SERVICE_URL = "http://localhost:18081/hystrix/ribbon";


    private Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;

    private String str;

    public HelloHystrixCommandManualCreate(Setter setter, RestTemplate restTemplate, String str) {
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
    }


    /**
     * 请求的同步执行
     *
     * @return
     * @throws Exception
     */
    @Override
    protected String run() throws Exception {
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);


        Setter set = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupName"));

        //请求的同步执行
        String helloResul = new HelloHystrixCommandManualCreate(set, restTemplate, "hello").execute();
        System.out.println("请求的同步执行 结果：" + helloResul);

        //异步执行
        Future<String> hm = new HelloHystrixCommandManualCreate(set, restTemplate, "hello").queue();
        // 异步结果
        String result = hm.get();
        System.out.println("异步执行 结果：" + result);
    }

}
