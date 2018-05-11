### 异常处理

#### 异常传播
在 HystrixCommand 实现的 run 方法中抛出异常时，除了 HystrixBadRequestException 之处，其他
异常均会被 hystrix 认为命令执行失败并触发服务降级的处理逻辑

#### 不触发 hystrix 服务降级处理
通过 ignoreExceptions  忽略指定异常类型功能，当调用方法抛出  BadRequestException 异常时，
hystrix 会将包装成 HystrixBadRequestException 中抛出，这样就不会触发后续的 fallback 逻辑

@HystrixCommand(ignoreExceptions = BadRequestException.class)


#### 获取触发服务降级的具体异常内容
在 fallback 增加 Throwable 参数 获取触发服务降级的具体异常内容