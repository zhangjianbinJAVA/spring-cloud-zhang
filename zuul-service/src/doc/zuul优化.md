http://www.jianshu.com/p/d401452fe76e

###zuul 参数调优

适用版本：

spring-boot: 1.4.x.RELEASE  spring-cloud：Camden.SR3  Hystrix: 1.5.6

spring-boot-tomcat 优化参数：

主要只有2个，最大和最小worker线程：
```
server.tomcat.max-threads=128 # Maximum amount of worker threads.
server.tomcat.min-spare-threads=64 # Minimum amount of worker threads.
spring-boot-undertow 优化参数：
```
###ioThreads

设置IO线程数, 它主要执行非阻塞的任务,它们会负责多个连接,默认取CPU核心数量,最小值为2。
Math.max(Runtime.getRuntime().availableProcessors(), 2);
- spring-boot 参数：server.undertow.io-threads=

###worker-threads

阻塞任务线程池, 当执行类似servlet请求阻塞操作, undertow会从这个线程池中取得线程,
它的值设置取决于系统的负载，默认值为 io-threads*8。

- spring-boot 参数：server.undertow.worker-threads=

###buffer
- buffer-size:
每块buffer的空间大小,越小的空间被利用越充分。
- buffers-per-region:
每个区分配的buffer数量 , 所以pool的大小是buffer-size * buffers-per-region。

###directBuffers
是否分配的直接内存。
获取JVM最大可用内存maxMemory=Runtime.getRuntime().maxMemory();

maxMemory<64M,不开启directBuffers， bufferSize = 512,buffersPerRegion = 10;

64<=maxMemory<128M,开启directBuffers， bufferSize = 1024 bytes,buffersPerRegion = 10;

maxMemory>128M,开启directBuffers， bufferSize = 16*1024 bytes,buffersPerRegion = 20;

###spring-boot 参数：
####最大可用内存<64M,不开启
- server.undertow.buffer-size= # Size of each buffer in bytes.
- server.undertow.buffers-per-region= # Number of buffer per region.
- server.undertow.direct-buffers= # Allocate buffers outside the Java heap.

//默认值：cpu数量，最小为2
server.undertow.io-threads= # Number of I/O threads to create for the worker.
//默认值：io-threads*8
server.undertow.worker-threads= # Number of worker threads.

###zuul 内置参数
- zuul.host.maxTotalConnections
适用于ApacheHttpClient，如果是okhttp无效。每个服务的http客户端连接池最大连接，默认是200.
- zuul.host.maxPerRouteConnections
适用于ApacheHttpClient，如果是okhttp无效。每个route可用的最大连接数，默认值是20。

- zuul.semaphore.max-semaphores
Hystrix最大的并发请求execution.isolation.semaphore.maxConcurrentRequests，
这个值并非TPS、QPS、RPS等都是相对值，指的是1秒时间窗口内的事务/查询/请求，
semaphore.maxConcurrentRequests是一个绝对值，无时间窗口，相当于亚毫秒级的。
当请求达到或超过该设置值后，其其余就会被拒绝。默认值是100。
参考: Hystrix semaphore和thread隔离策略的区别及配置参考

这个参数本来直接可以通过Hystrix的命名规则来设置，但被zuul重新设计了，
使得在zuul中semaphores的最大并发请求有4个方法的参数可以设置，
如果4个参数都存在优先级（1~4）由高到低：

1. zuul.eureka.api.semaphore.maxSemaphores
2. zuul.semaphore.max-semaphores
3. hystrix.command.api.execution.isolation.semaphore.maxConcurrentRequests
4. hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests

####需要注意的是：在Camden.SR3版本的
zuul中hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests
设置不会起作用，
这是因为在org.springframework.cloud.netflix.zuul.filters.ZuulProperties.HystrixSemaphore.maxSemaphores=100设置了默认值100，
因此zuul.semaphore.max-semaphores的优先级高于hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests。

zuul.eureka.[commandKey].semaphore.maxSemaphores：
其中commandKey为

- 参考设置参数：
```
zuul.host.maxTotalConnections: 200
zuul.host.maxPerRouteConnections: 10
#zuul.semaphore.max-semaphores: 128
# 建议使用这种方式来设置，可以给每个不同的后端微服务设置不同的信号量
zuul.[service id].semaphore.maxSemaphores: 128
```

- 其他Hystrix参数：
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds用来设置thread和semaphore两种隔离策略的超时时间，
默认值是1000。

建议设置这个参数，在Hystrix 1.4.0之前，semaphore-isolated隔离策略是不能超时的，从1.4.0开始semaphore-isolated也支持超时时间了。
建议通过CommandKey设置不同微服务的超时时间,
对于zuul而言，CommandKey就是service id：

hystrix.command.[CommandKey].execution.isolation.thread.timeoutInMilliseconds

- ribbon参数
```
ribbon:
#  # Max number of next servers to retry (excluding the first server)
#  MaxAutoRetries: 1
#  # Whether all operations can be retried for this client
#  MaxAutoRetriesNextServer: 1
#  # Interval to refresh the server list from the source
#  OkToRetryOnAllOperations: true
#  # Interval to refresh the server list from the source
#  ServerListRefreshInterval: 2000
#  # Connect timeout used by Apache HttpClient
  ConnectTimeout: 3000
#  # Read timeout used by Apache HttpClient
  ReadTimeout: 3000
主要是ConnectTimeout和ReadTimeout2个参数，最终会设置到http Client中。

```

####Hystrix semaphore和thread隔离策略的区别及配置参考
http://www.jianshu.com/p/b8d21248c9b1

通用设置说明

Hystrix所有的配置都是hystrix.command.[HystrixCommandKey]开头,
其中[HystrixCommandKey]是可变的，默认是default,
即hystrix.command.default；另外Hystrix内置了默认参数，如果没有配置Hystrix属性，默认参数就会被设置，其优先级：

1. hystrix.command.[HystrixCommandKey].XXX
2. hystrix.command.default.XXX
3. Hystrix代码内置属性参数值

####Hystrix隔离策略相关的参数

###策略参数设置
- execution.isolation.strategy= THREAD|SEMAPHORE
- execution.isolation.thread.timeoutInMilliseconds

hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds用来设置thread和semaphore两种隔离策略的超时时间，
默认值是1000。

建议设置这个参数，在Hystrix 1.4.0之前，semaphore-isolated隔离策略是不能超时的，从1.4.0开始semaphore-isolated也支持超时时间了。
建议通过CommandKey设置不同微服务的超时时间,对于zuul而言，
CommandKey就是service id：hystrix.command.[CommandKey].execution.isolation.thread.timeoutInMilliseconds
这个超时时间要根据CommandKey所对应的业务和服务器所能承受的负载来设置，要根据CommandKey业务的平均响应时间设置，
一般是大于平均响应时间的20%~100%,最好是根据压力测试结果来评估，这个值设置太大，会导致 (线程不够用)  而会导致太多的任务被fallback；
设置太小，一些特殊的慢业务失败率提升，甚至会造成这个业务一直无法成功，在重试机制存在的情况下，反而会加重后端服务压力。

- execution.isolation.semaphore.maxConcurrentRequests

这个值并非TPS、QPS、RPS等都是相对值，指的是1秒时间窗口内的事务/查询/请求，semaphore.maxConcurrentRequests是一个绝对值，无时间窗口，
相当于亚毫秒级的，指任意时间点允许的并发数。当请求达到或超过该设置值后，其其余就会被拒绝。默认值是100。

- execution.timeout.enabled
是否开启超时，默认是true，开启。

- execution.isolation.thread.interruptOnTimeout
发生超时是是否中断线程，默认是true。

- execution.isolation.thread.interruptOnCancel
取消时是否中断线程，默认是false。

