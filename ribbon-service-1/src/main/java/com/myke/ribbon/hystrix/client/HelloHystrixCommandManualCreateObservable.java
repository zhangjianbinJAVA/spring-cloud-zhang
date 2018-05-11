package com.myke.ribbon.hystrix.client;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 4
 *
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/8/4 10:21
 * <p>
 * 手动方式
 * <p>
 * 创建请求命令
 * <p>
 * 通过 Observable 来实现响应式执行方式
 * <p>
 * 通过继承的方式来实现
 */
public class HelloHystrixCommandManualCreateObservable extends HystrixCommand<String> {

    private static final String HELLO_SERVICE_URL = "http://localhost:18081/hystrix/ribbon";


    private Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;

    private String str;

    public HelloHystrixCommandManualCreateObservable(Setter setter, RestTemplate restTemplate, String str) {
        super(setter);
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


    /**
     * 虽然 HystrixCornrnand 具备了 observe() 和 toObservable()的功能 它返回的 Observable 只能发射一次数据
     *
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);


        Setter set = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupName"));

        // Hot Observable  该命令会在 observe() 调用的时候立即执行
        // 当 Observable 每次被订阅的时候会重放它的行为
        Observable<String> hello_result =
                new HelloHystrixCommandManualCreateObservable(set, restTemplate, "hello").observe();

        Future<String> stringFuture = hello_result.toBlocking().toFuture();
        System.out.println("Hot Observable 结果：" + stringFuture.get());

        // Cold Observable 该命令会在 toObservable () 执行之后，命令不会被立即执行
        // 当所有订阅者都订阅它之后才会执行
        Observable<String> result =
                new HelloHystrixCommandManualCreateObservable(set, restTemplate, "hello").toObservable();

        Future<String> resultFuture = result.toBlocking().toFuture();
        System.out.println("Cold Observable 结果：" + resultFuture.get());
    }

}
