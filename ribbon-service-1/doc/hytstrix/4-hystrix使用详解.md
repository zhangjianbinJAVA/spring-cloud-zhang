### 详细介绍 Hystrix 各接口 和 注解的 使用方法

### HystrixCommand 它用来封装具体的依赖服务调用逻辑

- 通过继承 HystrixCommand 的方式来实现 查看 HelloHystrixCommandManualCreate 类
  - 同步执行 execute()
  - 异步执行 queue()
  
- 通过注解 @HystrixCommand 的方式 查看 HelloHystrixCommandAnnotationCreate 类

- 使用 @HystrixCommand 注解实现响应式命令时 查看 HelloHystrixCommandAnnotationCreateObservable 类

- 手动 定义服务降级
在 HystrixCommand 中可以通过重载 getFallback() 方法来实现服务降级逻辑
Hystrix 会在 run()执行过程中出现 错误、超时、线程池拒绝、断路器熔断 等情况时，执行
getFallback()方法内的逻辑，

- 注解实现服务降级
使用 @HystrixCommand 中的 fallbackMethod 参数来指定具体的服务降级实现方法
> fallback 实现函数定义在同一个类中,由于必须定义在一个类中,对于 fallback 的访问修饰符没有特定的要求.
降级方法也可以使用 @HystrixCommand 中的 fallbackMethod 参数 再来指定 服务降级实现方法，因为第一个
服务降级可能是另外一个网络请求来荻取，所以也有可能失败，所以也要指定降级方法

### 也有一些情况可以不去实现降级逻辑
- 执行写操作的命令
执行方法 返回类型是void或是为空的Observable，当写入操作失败的时候，我们通常只需要通知调用者即可

- 执行批处理或离线计算的命令
通常这些操作只需要将错误传播给调用者，然后让调用者稍后重试而不是发送给调用者一
个静默的降级处理响应。


### 异常处理
- 异常传播 查看  HelloHystrixCommandAnnotationFallBack 类

在HystrixComrnand实现的run()方法中抛出异常时，除了 HystrixBadRequestException
之外，其他异常均会被 Hystrix 认为命令执行失败并触发服务降级的处理逻辑。

ignoreExceptions 参数 忽略指定异常类型功能，不触发 fallback 逻辑

- 异常获取

获取触发服务降级的具体异常内容


### 命令名称、分组以及线程池划分
#### 继承方式实现的 Hystrix 命令使用`类名作为默认的命令名称`,也可以在构造函数中通过 Setter 静态类来设置.
  查看  HelloHystrixCommandManualCreate 类

```

// withGroupKey 来设置命令组名   必需的参数
// andCommandKey 来设置命令名    个可选参数
super(Setter.withGroupKey(
        HystrixCommandGroupKey.Factory.asKey("groupName")).
        andCommandKey(HystrixCommandKey.Factory.asKey("commandName")));

```

#### 为什么一定要设置命令组呢？
- 设置命令组名 Hystrix 会根据组来组织和统计命令的告警、仪表盘等信息。
- Hystrix 命令默认的线程划分也是根据 命令分组 来实现的默认情况下，
  Hystrix 会让 相同组名 的命令使用同一个线程池.
- 所以我们需要在创建 Hystrix 命令时 为其指定 命令组名 来实现 默认的线程池 划分  
- Hystrix 还提供了 HystrixThreadPoolKey 来对线程池进行设置实现更细粒度的线程池划分.

```
// withGroupKey 来设置命令组名
// andCommandKey 来设置命令名
// andThreadPoolKey 实现更细粒度的 线程池 划分
super(Setter.withGroupKey(
             HystrixCommandGroupKey.Factory.asKey("groupName")).
             andCommandKey(HystrixCommandKey.Factory.asKey("commandName"))
             .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("threadPoolKey")));
```
- 如果在没有特别指定 HystrixThreadPoolKey 的情况下，依然会使用命令组的方式
  来划分线程池，通常情况下，尽量通过 HystrixThreadPoolKey 的方式来指定线程池的划分
  
#### 注解方式实现  命令名称、分组以及线程池划分
  
```
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
```  


#### 请求缓存  查看  HelloHystrixCommandManualCreateEnableCache 类
在高并发的场景之下，Hystrix 中提供了请求缓存的功能

- 开启请求缓存功能
  通过重载 getCacheKey()方法来开启请求缓存,当不同的外部请求处理逻辑调用了同
  一个依赖服务时，Hystrix 会根据 getCacheKey 方法 返回的值 来区分
  是否是重复的请求，如果它们的 cacheKey相同，那么该依赖服务只会在第
  一个请求到达时被真实地调用一次，另外一个请求则是直接从请求缓存中返回结果.
  
  开启请求缓存优点
  - 减少重复的请求数，降低依赖服务的并发度。
  - 在同一用户请求的上下文中，相同依赖服务的返回数据始终保持
    一致
  - 请求缓存在run()和 construct()执行之前生效， 所以可以有效减少不必要的线程开销

- 清理失效缓存功能
  
  可以通过 HystrixRequestCache.clear() 方法来进行缓存的清理 
  
  
#### 请求缓存工作原理  
getCacheKey 方法 在 AbstractCommand 类中，所以我们可以先从这个抽象命令类的实现中看起。

- 尝试获取请求缓存 
  
  根据 key 的值去调用 HystrixRequestCache 中的 get 来获取 hystrixObservable 对象 

- 将请求结果加入缓存
  hystrixObservable 为延迟执行的命令结果对象

```
Observable<R> hystrixObservable = Observable.defer(applyHystrixSemantics).map(wrapWithAllOnNextHooks);
                    Observable afterCache;
                    // 先判断当前命令是否开启了请求缓存功能
                    if (requestCacheEnabled && cacheKey != null) {
                    
                        // hystrixObservable 对象包装成请求缓存结果 HystrixCachedObservable 的实例对象 toCache
                        // 缓存对象 HystrixRequestCache 中维护了一个线程安全的Map来保存请求缓存的响应                       
                        HystrixCachedObservable<R> toCache = HystrixCachedObservable.from(hystrixObservable, AbstractCommand.this);
                        // 调用 putIfAbsent 将包装的请求缓存放入缓存对象
                        HystrixCommandResponseFromCache<R> fromCache = (HystrixCommandResponseFromCache)AbstractCommand.this.requestCache.putIfAbsent(cacheKey, toCache);
                        
                        // 如果其不为null,说明当前缓存Key的请求命令缓存命中
                        if (fromCache != null) {
                           // 直接对 toCache 执行取消订阅操作（即，不再发起真实请求）
                            toCache.unsubscribe();
                            AbstractCommand.this.isResponseFromCache = true;
                            // 返回缓存中的结果
                            return AbstractCommand.this.handleRequestCacheHitAndEmitValues(fromCache, AbstractCommand.this);
                        }
                         
                        // 没有命中缓存， toCache 将其转换成 Observable 返回给调用者使用，发起真实的请求
                        afterCache = toCache.toObservable();
                    } else {
                        afterCache = hystrixObservable;
                    }

                    return afterCache.doOnTerminate(terminateCommandCleanup).doOnUnsubscribe(unsubscribeCommandCleanup).doOnCompleted(fireOnCompletedHook);
         
```  

#### 使用注解实现请求缓存
- @CacheKey

该注解用来标记请求命令返回的结果应该被缓存， 它必须与 @HystrixCommand 注解结合使用

@CacheKey注解除了可以指定方法参数作为缓存Key之外，它还允许访问参数对象的内部属性作为缓存 Key
```
@CacheResult 
@HystrixCommand 
public User getUserByid(@CacheKey("id") User user) { // 它指定了User对象的id属性作为缓存Key
}
```

- @CacheRemove()

该注解用来让请求命令的缓存失效，失效的缓存根据定义的 Key 决定

- @CacheResult
该注解用来在诮求命令的参数上标记， 使其作为缓存的Key值，如果没有标注则会使用所有参数。如果同时还使用了
@CacheResult 和 @CacheRemove 注解的 cacheKeyMethod 方法指定缓存 Key 的生成，那么该注解将不会起作用
> JSR 107 是Java缓存API的定义，也被称为 ]Cache
    
    
#### 请求合并

微服务架构中的依赖通常通过远程调用实现，而远程调用中最常见的问题就是通信消
耗与连接数占用。在高并发的情况之下，`因通信次数的增加，总的通信时间消耗将会变得
不那么理想。同时，因为依赖服务的线程池资源有限，将出现排队等待与响应延迟的清况`。
为了优化这两个问题，Hystrix提供了 HystrixCollapser 来实现请求的合并，以减少通
信消耗和线程数的占用.

`HystrixCollapser 实现了在HystrixCommand之前放置一个合并处理器，将处千一个很短的时间窗（默认10毫秒）内对同一
依赖服务的多个请求进行整合并以批量方式发起请求的功能（服务提供方也需要提供相应的批量实现接口）`。 
通过 HystrixCollapser 的封装，开发者不需要关注线程合并的细节过程，只需关注批量化服务和处理。   

```
 // 该函数用来定义获取请求参数的方法
 public abstract RequestArgumentType getRequestArgument();
 
 // 合并请求产生批量命令的具体实现方法
 protected abstract HystrixCommand<BatchReturnType> createCommand(Collection<CollapsedRequest<ResponseType, RequestArgumentType>> requests);
 
 // 批量命令结果返回后的处理，这里需要实现将批量结果拆分并传递给合并前的各个原子请求命令的逻辑
 protected abstract void mapResponseToRequests(BatchReturnType batchResponse, Collection<CollapsedRequest<ResponseType, RequestArgumentType>> requests);

```

从 HystrixCollapser 抽象类的定义中可以看到, 它指定了三个不同的类型
- BatchReturnType:  合并后批量请求的返回类型
- ResponseType: 单个请求返回的类型。
- RequestArgumentType: 请求参数类型。

```
   @HystrixCollapser(batchMethod = "getHelloStr_Thread",collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds",value = "100")})
    @HystrixCommand(commandKey = "getHelloStr_Thread", groupKey = "groupKey", threadPoolKey = "threadKey")
    public String getHelloStr_Thread(String str) {
        logger.info(" 同步执行的实现 执行 getHelloStr_1 ……");
        return restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello?str={1}", String.class, str).getBody();
    }
```

请求合并器的延迟时间窗会带来额外开销
- 请求命令本身的延迟
- 延迟时间窗内的并发量
