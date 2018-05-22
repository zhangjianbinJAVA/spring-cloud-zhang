### feign client 分析
Client 接口  
LoadBalancerFeignClient feign 负载均衡器   
FeignAutoConfiguration  feign 自动配置  

## feign 和 ribbon 自动化配置类
FeignRibbonClientAutoConfiguration

默认使用 HttpURLConnection
feign.Client.Default 

### feign okhttp
```
  <!-- 使用feign-okhttp替换Feign原生httpclient -->
        <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-okhttp</artifactId>
        </dependency>
```
### feign ApacheHttpClient
```
  <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-httpclient</artifactId>
  </dependency>
```

### feign 请求配置
```
连接时间
connectTimeoutMillis:2000

读取超时时间
readTimeoutMillis:5000

public Options(int connectTimeoutMillis, int readTimeoutMillis){
      this.connectTimeoutMillis = connectTimeoutMillis;
      this.readTimeoutMillis = readTimeoutMillis;
   }
   
@Bean
public Request.Options options() {
        return new Request.Options(1000,60000);
}   
```

### feign 拦截器接口类
RequestInterceptor

### feign 压缩配置 ApacheHttpClient 客户端下有效
FeignAcceptGzipEncodingAutoConfiguration

### feign request option 默认继承 ribbon 的配置，所以配置 ribbon 的配置项 也能覆盖 feign 的配置项
FeignOptionsClientConfig extends DefaultClientConfigImpl

### feign　请求执行逻辑
1. FeignLoadBalancer#execute   
2. LoadBalancerFeignClient#execute

### RetryTemplate 的重试处理类
RetryableFeignLoadBalancer


CachingSpringLoadBalancerFactory 类中 当有 RetryTemplate 依赖时，重试自动开启
```
public FeignLoadBalancer create(String clientName) {
		if (this.cache.containsKey(clientName)) {
			return this.cache.get(clientName);
		}
		IClientConfig config = this.factory.getClientConfig(clientName);
		ILoadBalancer lb = this.factory.getLoadBalancer(clientName);
		ServerIntrospector serverIntrospector = this.factory.getInstance(clientName, ServerIntrospector.class);
		
		// enableRetry：是否开启重试  RetryableFeignLoadBalancer.execute 执行请求
		FeignLoadBalancer client = enableRetry ? new RetryableFeignLoadBalancer(lb, config, serverIntrospector,
				loadBalancedRetryPolicyFactory) : new FeignLoadBalancer(lb, config, serverIntrospector);
		this.cache.put(clientName, client);
		return client;
	}
```

### feign 请求配置超时信息逻辑
当使用feign 的请求默认配置时，
会被 DefaultClientConfigImpl 类中的默认值覆盖，也就是ribbon 的默认配置
如果不使用 feign 请求的默认配置时，将使用 自定义 Request.Options 的 bean配置

```
LoadBalancerFeignClient 类

IClientConfig getClientConfig(Request.Options options, String clientName) {
		IClientConfig requestConfig;
		if (options == DEFAULT_OPTIONS) {
			requestConfig = this.clientFactory.getClientConfig(clientName);
		} else {
			requestConfig = new FeignOptionsClientConfig(options);
		}
		return requestConfig;
	}
```



```
protected <T, E extends Throwable> T doExecute(RetryCallback<T, E> retryCallback,
			RecoveryCallback<T> recoveryCallback, RetryState state) throws E,
			ExhaustedRetryException {
			
			// retryPolicy 重试的策略
			// context 重试的原因
			while (canRetry(retryPolicy, context) && !context.isExhaustedOnly())
		}

```

## LoadBalancerContext 类中有默认重试次数 当工程中有 spring-retry的依赖时，会使用该类处理重试逻辑
### RibbonLoadBalancedRetryPolicyFactory 查看哪些请求方式需要重试
```
@Override
    public LoadBalancedRetryPolicy create(final String serviceId, final ServiceInstanceChooser loadBalanceChooser) {
        final RibbonLoadBalancerContext lbContext = this.clientFactory
                .getLoadBalancerContext(serviceId);
        return new LoadBalancedRetryPolicy() {
            private int sameServerCount = 0;
            private int nextServerCount = 0;
            private ServiceInstance lastServiceInstance = null;
            public boolean canRetry(LoadBalancedRetryContext context) {
                HttpMethod method = context.getRequest().getMethod();
                return HttpMethod.GET == method || lbContext.isOkToRetryOnAllOperations();
            }

            @Override
            public boolean canRetrySameServer(LoadBalancedRetryContext context) {
                return sameServerCount < lbContext.getRetryHandler().getMaxRetriesOnSameServer() && canRetry(context);
            }

            @Override
            public boolean canRetryNextServer(LoadBalancedRetryContext context) {
                //this will be called after a failure occurs and we increment the counter
                //so we check that the count is less than or equals to too make sure
                //we try the next server the right number of times
                return nextServerCount <= lbContext.getRetryHandler().getMaxRetriesOnNextServer() && canRetry(context);
            }

            @Override
            public void close(LoadBalancedRetryContext context) {

            }

            @Override
            public void registerThrowable(LoadBalancedRetryContext context, Throwable throwable) {
                //Check if we need to ask the load balancer for a new server.
                //Do this before we increment the counters because the first call to this method
                //is not a retry it is just an initial failure.
                if(!canRetrySameServer(context)  && canRetryNextServer(context)) {
                    context.setServiceInstance(loadBalanceChooser.choose(serviceId));
                }
                //This method is called regardless of whether we are retrying or making the first request.
                //Since we do not count the initial request in the retry count we don't reset the counter
                //until we actually equal the same server count limit.  This will allow us to make the initial
                //request plus the right number of retries.
                if(sameServerCount >= lbContext.getRetryHandler().getMaxRetriesOnSameServer() && canRetry(context)) {
                    //reset same server since we are moving to a new server
                    sameServerCount = 0;
                    nextServerCount++;
                    if(!canRetryNextServer(context)) {
                        context.setExhaustedOnly();
                    }
                } else {
                    sameServerCount++;
                }

            }
        };
    }
```

## feign 的重试都会执行 
FeignLoadBalancer 类中的 getRequestSpecificRetryHandler 函数逻辑
```
@Override
	public RequestSpecificRetryHandler getRequestSpecificRetryHandler(
			RibbonRequest request, IClientConfig requestConfig) {
		//所有请求操作都重试	
		if (this.clientConfig.get(CommonClientConfigKey.OkToRetryOnAllOperations,
				false)) {
			return new RequestSpecificRetryHandler(true, true, this.getRetryHandler(),
					requestConfig);
		}
		//不是 get 方法时
		if (!request.toRequest().method().equals("GET")) {
			return new RequestSpecificRetryHandler(true, false, this.getRetryHandler(),
					requestConfig);
		}
		else {
		    //默认重试所有请求操作
			return new RequestSpecificRetryHandler(true, true, this.getRetryHandler(),
					requestConfig);
		}
	}

```


### Feign如果想要使用Hystrix Stream，需要做一些额外操作

我们知道Feign本身就是支持Hystrix的，可以直接使用@FeignClient(value = "microservice-provider-user", fallback = XXX.class) 
来指定fallback的类，这个fallback类集成@FeignClient所标注的接口即可。


但是假设我们需要使用Hystrix Stream进行监控，默认情况下，访问http://IP:PORT/hystrix.stream 是个404。
如何为Feign增加Hystrix Stream支持呢？

第一步：添加依赖
```
<!-- 整合hystrix，其实feign中自带了hystrix，
引入该依赖主要是为了使用其中的hystrix-metrics-event-stream，用于 dashboard -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

第二步：在启动类上添加@EnableCircuitBreaker 注解
```
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableCircuitBreaker
public class MovieFeignHystrixApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieFeignHystrixApplication.class, args);
  }
}
```

### 如果需要自定义单个Feign配置，Feign的@Configuration 

注解的类不能与@ComponentScan 的包重叠,如果包重叠，将会导致所有的Feign Client都会使用该配置。
