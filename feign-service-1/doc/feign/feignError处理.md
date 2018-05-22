### feign client对restful调用的异常处理
SynchronousMethodHandler 类包括 feign 客户端 方法的执行逻辑和异常处理逻辑

其中对status code的处理见这段
```
   if (response.status() >= 200 && response.status() < 300) {
        if (void.class == metadata.returnType()) {
          return null;
        } else {
          return decode(response);
        }
      } else if (decode404 && response.status() == 404) {
        return decoder.decode(response, metadata.returnType());
      } else {
        throw errorDecoder.decode(metadata.configKey(), response);
      }
```

也就是feign client的处理跟 nginx的是不一样的，feign client把非200的以及404(可以配置是否纳入异常)都算成error，都转给 errorDecoder 去处理了。

### 注意：
要特别注意，对于restful抛出的4xx的错误，也许大部分是业务异常，
并不是服务提供方的异常，因此在进行feign client调用的时候，需要进行 errorDecoder 去处理，
适配为 HystrixBadRequestException，好避开 circuit breaker 的统计，否则就容易误判，
传几个错误的参数，立马就熔断整个服务了，后果不堪设想。

### errorDecoder 实例
```
@Configuration
public class BizExceptionFeignErrorDecoder implements feign.codec.ErrorDecoder{

    private static final Logger logger = LoggerFactory.getLogger(BizExceptionFeignErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        if(response.status() >= 400 && response.status() <= 499){
            return new HystrixBadRequestException("xxxxxx");
        }
        return feign.FeignException.errorStatus(methodKey, response);
    }
}

```

### 异常图
![Img_text](img/feign异常.png)


### 使用 Hystrix 解决内部调用抛出异常问题
Hystrix如何处理异常的 代码位置：com.netflix.hystrix.AbstractCommand#executeCommandAndObserve

```
//省略部分代码
 private Observable<R> executeCommandAndObserve(final AbstractCommand<R> _cmd) {
//省略部分代码
        final Func1<Throwable, Observable<R>> handleFallback = new Func1<Throwable, Observable<R>>() {
            @Override
            public Observable<R> call(Throwable t) {
                Exception e = getExceptionFromThrowable(t);
                executionResult = executionResult.setExecutionException(e);
                if (e instanceof RejectedExecutionException) {
                    return handleThreadPoolRejectionViaFallback(e);
                } else if (t instanceof HystrixTimeoutException) {
                    return handleTimeoutViaFallback();
                } else if (t instanceof HystrixBadRequestException) {
                    return handleBadRequestByEmittingError(e);
                } else {
                    /*
                     * Treat HystrixBadRequestException from ExecutionHook like a plain HystrixBadRequestException.
                     */
                    if (e instanceof HystrixBadRequestException) {
                        eventNotifier.markEvent(HystrixEventType.BAD_REQUEST, commandKey);
                        return Observable.error(e);
                    }

                    return handleFailureViaFallback(e);
                }
            }
        };
//省略部分代码
    }
该类中该方法为发生异常的回调方

```

该类中该方法为发生异常的回调方法，由此可以看出如果抛出异常如果是 HystrixBadRequestException 
是直接处理异常之后进行抛出（这里不会触发熔断机制），而不是进入回调方法。

#### 解决方案
那么我们对于请求异常的解决方案就需要通过 HystrixBadRequestException 来解决了（不会触发熔断机制），
根据返回响应创建对应异常并将异常封装进 HystrixBadRequestException，
业务系统调用中取出 HystrixBadRequestException 中的自定义异常进行处理，封装异常说明