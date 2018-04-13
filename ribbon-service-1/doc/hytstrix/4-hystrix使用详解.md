### 详细介绍 Hystrix 各接口 和 注解的 使用方法

### HystrixCommand 它用来封装具体的依赖服务调用逻辑

- 通过继承 HystrixCommand 的方式来实现 查看 HelloHystrixCommandManualCreate 类
  - 同步执行 execute()
  - 异步执行 queue()
  
- 通过注解 @HystrixCommand 的方式 查看 HelloHystrixCommandAnnotationCreate 类
