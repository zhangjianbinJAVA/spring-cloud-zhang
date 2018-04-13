### github
https://github.com/Netflix/Hystrix

### hystrix 工作流程图
![img_text](.\img\hystrix原理.png)

### 1.创建HystrixCommand or HystrixObservableCommand 对象

这两个对象来表示依赖服务的操作请求，同时传递所有需要的参数，它采用了命令模式 来实现对服务调用操作的封装，
这两个 Command 对象分别针对不同的应用场景
- HystrixCommand ：用在依赖的服务返回单个操作结果的时候
- HystrixObservableCommand ：用在依赖的服务返回多个操作结果的时候

### 2. 命令模式
hystrix 一共存在4种命令的执行方式，hystrix 在执行时会根据创建的 Command 对象以及具体的情况选择一个执行

HystrixCommand 两种执行方式
-  R execute() 同步执行  返回一个单一的结果对象
- Future<R> queue() 异步执行 返回一个Future对象，包含的单一结果对象

HystrixObservableCommand 两种执行方式
- Observable<R> observe()    代表了操作的多个结果，它是一个Hot Observable ([əb'zɜːvəbl])
- Observable<R> toObservable() 代表了操作的多个结果,它是一个 Cold Observable

### Hot Observable 订阅者只能看到整个操作的局部过程

Hot Observable 对应 command.observe(),不论 事件源 是否有订阅者 都会在创建后对事件进行发布
所以 每一个订阅者 都有可能是从事件源的中途开始的，只能看到整个操作的局部过程

### Cold Observable 订阅者从一开始看到了整个操作的全部过程
Cold Observable 对应 command.toObervable(),在没有订阅者的时候并不会发布事件，而是进行等待，
直到有订阅者之后才发布事件，所以保证订阅者从一开始看到了整个操作的全部过程

### RxJava 观察者--订阅者
> RxJava是一个 通过拓展的观察者模式 使用 线程调度器 来解决异步操作 进行事件传递 的库   
> https://blog.csdn.net/qq_33553515/article/details/62898070

Observable 对象就是 RxJava 中的核心内容之一，为事件源或被观察者 对应的 Subscriber 对象，为订阅者或观察者

- Observable 用来向订阅者Subscriber对象发布事件，这里所指的事件通常就是对依赖服务的调用
- Observable 对象每发布一个事件，就会调用对应观察者Subscriber对象的 onNext(T t) 方法
- 每个 Observable 的执行，最后一定会通过调用 Subscriber.onCompleted() 或者 Subscriber.onError(Throwable e) 
  来结束该事件的操作流
- 通过 Observable.subscribe(subscribe) 来触发事件的发布


- execute ()是通过 queue()返回的异步对象 Future<R> 的 get()方法来实现同步执行的。该方法会等待任务执行结束，然后获得R类型的结果进行返回。

- queue () 则是通过 toObservable()来获得一个 Cold Observable, 并且通过 toBlocking ()将该 Observable 转换成 BlockingObservable,
  它可以把数据以阻塞的方式发射出来。而 toFuture方法 则是把 BlockingObservable 转 换为一个 Future，该方法只是创建一个 Future 返回并不会阻塞。
  这使得消费者可以自己决定如何处理异步操作。
  
  而 execute()就是直接使用了 queue() 返回的 Future 中的阻塞方法 get()来实现同步操作的。
  
  同时通过这种方式转换的 Future 要求 Observable 只发射一个数据，所以这两个实现都只能返回单一结果。
  

```
    HystrixCommand 类
    
    
    public R execute() {
        try {
            Future的get方法是一个同步方法，如果未拿到结果或者未超时，主线程则一直等待
            return queue().get();
        } catch (Exception e) {
            throw decomposeException(e);
        }
    }
    
    Future 该接口用来返回异步的结果
    public Future<R> queue() {
        //返回 Cold Observable,把数据以阻塞的方式发射出来
        final Future<R> delegate = toObservable().toBlocking().toFuture();        	
        final Future<R> f = new Future<R>() {
            
        }
    }
    
R             value   = command.execute();
Future<R>     fValue  = command.queue();
Observable<R> ohValue = command.observe();         //hot observable
Observable<R> ocValue = command.toObservable();    //cold observable    
```

### 3. 结果是否被缓存   
若当前命令的请求缓存功能是被启用的，并且该命令缓存命中，那么缓存结果会立即以 Observable 对象的形式返回

### 4. 断路器是否打开  
 - 如果断路器是打开的，那么 hystrix 不会执行命令，则是转到 fallback处理逻辑 对应第8步
 - 如果断路器是关闭的，那么 hystrix 跳到 第5步，检查是否有可用资源来执行命令
 
### 5. 线程池/请求队列/信号量是否占满
占满，则转到 fallback 处理逻辑，hystrix 所判断线程池，是每个依赖服务的专有线程池，这种方式称为
舱壁模式 bulkhead pattern 来隔离每个依赖的服务。

### 6. HystrixCommand.run() 或 HystrixObservableCommand.construct()
- Hystrix会根据我们编写的方法来决定采取什么样的方式去请求依赖服务
- HystrixCommand.run() 返回一个单一的结果，或者抛出异常
- HystrixObservableCommand.construct() 返回一个 Observable 对象来发射多个结果，或通过 onError发送错误通知
> 如果方法的执行时间超过了命令设置的超时阈值，当前线程将会抛出一个 TimeoutException
  （如果该命令不在其自身的线程中执行，则会通过单独的计时线程来抛出），这种情况下转接到 fallback 处理逻辑（第8步）
  同时，如果当前命令没有被取消或中断，那么它最终忽略 run() 或 construct() 方法的返回
> 如果命令没有抛出异常并返回了结果，那么，hystrix在记录一些日志并采集监控报告之后将该结果返回。
> 在使用run()的情况下，hystrix 会返回一个 Observable，它发射单个结果并产生 onCompleted 的结束通知
> 而在使用 construct() 的情况下，hystrix 会直接返回该方法产生的  Observable 对象

### 7. 计算断路器的健康度
hystrix 将 成功、失败、拒绝、超时 等信息报告给断路器，而断路器会维护一组计数器来统计这些数据.
断路器会使用这些统计数据来决定是否要将断路器打开，来对某个依赖服务的请求进行熔断/短路直到恢复期结束。
若在恢复期结束后，根据统计数据判断如果还是未达到健康指标，就再次熔断/短路.
                     

### 8. fallback 处理 称为服务降级
当命令执行失败的时候，Hystrix 会进入 fallback 尝试回退处理也称该操作为服务降级
- 第4步，当前命令处于熔断/短路状态，断路器是打开的时候
- 第5步，当前命令的 线程池、请求队列 或者 信号量 被占满的时候
- 第6步，HystrixCommand.run() 或 HystrixObservableCommand.construct() 方法抛出异常时
> 在服务降级逻辑中，我们需要实现一个通用的响应结果，并且该结果的处理逻辑应当是从缓存或是根据一
些静态逻辑来获取，而不是依赖网络请求获取.如果一定要在降级逻辑中包含网络请求，
那么该请求也必须被包装在 HystrixCommand 或是 HystrixObservableCommand 中，从而形成级联的降级策略，
而最终的降级逻辑一定不是一个依赖网络请求的处理，而是一个能够稳定地返回结果的处理逻辑。

### 实现服务降级逻辑
- 使用 HystrixCommand 时，通过实现 HystrixCommand.getFallback()来实现服务降级逻辑
- 使用 HystrixObservableCommand 时， HystrixObservableCommand.resumeWithFallback()实现服务降级逻辑

### 如果降级失败的时候，hystrix 会根据不现的执行方法做出不同的处理
- execute() 抛出异常
- queue() 正常返回 Future对象，但是当调用get()来获取结果的时候抛出异常
- observe() 正常返回 Observable 对象，当订阅它的时候，将立即通过调用订阅者的 onError 方法通知中止请求
- toObservable() 正常返回 Observable 对象，当订阅它的时候，将通过调用订阅者的 onError 方法来通知中止请求

### 9. 返回成功的响应
当hystrix 命令处理执行成功之后，将处理结果直接返回或是以 Observable的形式返回，具体以哪种方式返回取决
于第2步中所提到的命令的 4种不同执行方式

![img_txt](./img/hystrix-return-flow.png)

- toObservable() 返回最原始的 Observable ，必须通过订阅者才会真正触发命令的执行流程
- observe() 在 toObservable 产生原始 Observable 之后立即订阅它，让命令能够马上开始异步执行，并返回
一个Observable对象，当调用它的 subscribe 时，将重新产生结果和通知给订阅者
- queue() 将toObservable()产生的原始 Observable 通过 toBlocking()方法转换成 BlockingObservable 对象
，并调用它的 toFuture() 方法返回异步的Future 对象
- execute() 在queue 产生异步结果 Future对象之后，通过调用get()方法阻塞并等待结果的返回







