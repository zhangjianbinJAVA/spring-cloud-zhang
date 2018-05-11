### Feign的源码地址：https://github.com/OpenFeign/feign

### Feign简介
整合了SpringCloud Ribbon与 SpringCloud Hystrix, 除了提供这两者的强大功能之外，它还提
供了一种声明式的 Web服务客户端定义方式。它使得写web服务变得更简单。

使用Feign,只需要创建一个接口并注解。它具有可插拔的注解特性，包括 Feign 注解和JAX-RS注解。
Feign同时支持可插拔的编码器和解码器。spring cloud对 Spring mvc添加了支持，
同时在 spring web中使用相同的 HttpMessageConverter。

当我们使用feign的时候，spring cloud 整合了 Ribbon 和 eureka 去提供负载均衡。

简而言之：
- feign采用的是接口加注解
- feign 整合了ribbon

```
// @FeignClient 指定服务名来绑定服务
// 使用SpringMVC的注解来绑定具体该服务提供的REST接口

// 这里服务名不区分大小写
@FeignClient("HELLO-SERVICE-1")
public interface HelloFeignClient {
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    String index();
}
```

> Feign 实现的消费者，依然是利用 Ribbon 维护了针对 HELLO-SERVICE-1 的服务
列表信息，并且通过轮询实现了客户端负载均衡

### 参数绑定
参考 HelloFeignClient 类
