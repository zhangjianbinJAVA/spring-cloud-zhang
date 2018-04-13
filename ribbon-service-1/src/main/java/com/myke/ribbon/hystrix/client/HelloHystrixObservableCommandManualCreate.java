package com.myke.ribbon.hystrix.client;


import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/8/4 10:21
 * <p>
 * 手动方式
 * <p>
 * 创建请求命令
 * <p>
 * HystrixObservableCornrnand 通过它实现的命令可以获取能发射多次的 Observable
 * <p>
 * 如果使用 HystrixObservableCornrnand 来实现命令封装，需要将命令的执行逻辑在
 * construct 方法中重载，这样Hystrix才能将具体逻辑包装到Observable内
 */
public class HelloHystrixObservableCommandManualCreate extends HystrixObservableCommand<String> {

    private static final String HELLO_SERVICE_URL = "http://localhost:18081/hystrix/ribbon";

    private RestTemplate restTemplate;

    private String str;

    public HelloHystrixObservableCommandManualCreate(Setter setter, RestTemplate restTemplate, String str) {
        super(setter);
        this.restTemplate = restTemplate;
        this.str = str;
    }

    /**
     * 相当于创建 事件源
     *
     * @return
     */
    @Override
    protected Observable<String> construct() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    // 订阅者 或者观察者 是否订阅这个 事件源
                    if (!subscriber.isUnsubscribed()) {
                        // 执行请求
                        String result = restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();

                        //传播 执行请求的结果
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                } catch (RestClientException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

            }
        });
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
                new HelloHystrixObservableCommandManualCreate(set, restTemplate, "hello").observe();

        Future<String> stringFuture = hello_result.toBlocking().toFuture();
        System.out.println("Hot Observable 结果：" + stringFuture.get());

        // Cold Observable 该命令会在 toObservable () 执行之后，命令不会被立即执行
        // 当所有订阅者都订阅它之后才会执行
        Observable<String> result =
                new HelloHystrixObservableCommandManualCreate(set, restTemplate, "hello").toObservable();

        Future<String> resultFuture = result.toBlocking().toFuture();
        System.out.println("Cold Observable 结果：" + resultFuture.get());
    }


}
