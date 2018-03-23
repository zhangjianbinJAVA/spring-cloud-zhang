##API 网关
- 在api 网关服务上进行统一调用来对微服务接口做前置过滤，以实现对微服务接口的拦截和校验

###zuul 相关依赖
- spring-cloud-starter-hystrix

用来在网关服务中实现对微服务的转发时候的保护机制，通过线程隔离和断路器，防止微服的故障引发api网关资源无法
释放，从而影响其他应用的对外服务

- spring-cloud-starter-ribbon

用来实现在网关服务进行路由转发时候的客户端负载均衡以及请求重试

### zuul 总结 
zuul 天生就拥有线程隔离和断路器的自我保护功能，以及对服务调用的客户端负载均衡功能，
path和url的映射关系配置的路由规则时，请求不会采用 HystrixCommand来包装，所以这类
路由请求没有线程隔离和断路器的保护，并且也不会有负载均衡能力


###请求过滤器
- filterType 过滤器的类型  
    * pre：可以在请求被路由之前调用
    * routing：在路由请求时候被调用
    * post：在routing和error过滤器之后被调用
    * error：处理请求时发生错误时被调用
    
- filterOrder：通过int值来定义过滤器的执行顺序
    * 当请求在一个阶段中存在多个过滤器时，需要指定值来依次执行
    
- shouldFilter 返回一个boolean类型来判断该过滤器是否要执行
    
- run 过滤器的具体逻辑
    ctx.setSendZuulResponse(false); //不进行路由
    

##路由详解
###不使用eureka时
```
zuul.routes.api-a.path=/hello/**
zuul.routes.api-a.serviceId=hello-service
//不使用eureka
ribbon.eureka.enabled=false
//服务实例地址
hello-service.ribbon.ListOfServers=http://127.0.0.1:8080,http://127.0.0.1:8081
```
###服务路由的默认规则
采用服务名作为外请求的前缀
```
对所有服务都不自动创建路由规则，只有在配置文件中出现的映射规则才会被创建
zuul.ignored-services=*
```

###自定义路由规则 例如 userservice-v1 转化为 v1/userservice
```
    /**
     * 没有匹配上的服务名，则使用默认的路由规则
     * @return
     */
    @Bean
    public PatternServiceRouteMapper serviceRouteMapper() {
        return new PatternServiceRouteMapper(//
                "(?<name>^.+)-(?<version>v.+$)", // 匹配服务名的正则表达式
                "${version}/${name}"); //根据服务名中定义的内容转换出的路径表达式
    }
```

### 通配符
- "?"  匹配任意单个字符
- "*"   匹配任意数量的字符
- "**"  匹配任意数量的字符，支持多级目录


### RouteLocator
```
//路由匹配算法接口
Route getMatchingRoute(String path);
```
###SimpleRouteLocator
```
//路由规则加载算法，路由规则的保存是有序的，将配置文件中的路由规则依次加入到LinkedHashMap
protected Map<String, ZuulRoute> locateRoutes() {
		LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String, ZuulRoute>();
		for (ZuulRoute route : this.properties.getRoutes().values()) {
			routesMap.put(route.getPath(), route);
		}
		return routesMap;
	}

properties的配置内容无法保证有序，所以采用 YAML，实现有序的路由规则
解决问题 ：一个url 可能被多个不同路由的表达式匹配 
zuul:
  routes:
    hello-service-ext:
      path: /hello-service/ext/**
      serviceId: hello-service-ext
    hello-service:
      path: /hello-service/**
      serviceId: hello-service      	
```

###作用于全局  忽略表达式
不希望被 api 网关进行路由的 url 表达式
```
路径上带 hello 字符的是不会被路由的
zuul.ignored-patterns=/**/hello/**
```

###路由前缀
```
#作用于全局 为路由规则添加前缀 
注意：避免 路由表达式的起始字符与zuul.prefix 的参数相同
zuul.prefix=/api

访问时，/api/路由url 才能被路由到服务实例上，默认当路由时会移除 代理前缀 strip-prefix=true

对指定路由关闭代理移除代理前缀的动作,这样就会找不到url资源
zuul.routes.feign-hello-api.strip-prefix=false

```

###zuul debug 开启
- DebugFilter 执行条件：
  
  属性配置 zuul.debug.request=true 或 请求url 加参数 debug=true,
- 获取debug信息
  
  RequestContext.getCurrentContext().get("routingDebug")
  
- 开启route debug 的debug日志 写在 response header 中,属性 X-Zuul-Debug-Header

  zuul.include-debug-header=true

```
依赖 和 lombok 插件
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.18</version>
            <scope>provided</scope>
        </dependency>
        
```

###本地跳转
```
#网关 controller 处理
zuul.routes.gw-api=/gw-api/**
zuul.routes.gw-api.url=forward:/gw/local
```

###请求头敏感信息
```
默认情况下 zuul 在请求路由时，会过滤掉 http 请求头信息 Cookie,Set-Cookie,Authorization
通过设置全局参数为空 覆盖默认值，这样 Cookie,Set-Cookie,Authorization 都会被传递到下游服务端
zuul.sensitive-headers=
```

###微服务重定向时，请求的host设置为网关的ip和端口
zuul.add-host-header=true
```
PreDecorationFilter 的实现过过滤器链增加了host 信息

ZuulProxyConfiguration 中实例化了该 filter
```

### Hystrix 和 Ribbon 的支持
* 可以通过 Hystrix 和 Ribbon 的参数来调整路由请求的各种超时时间配置

当路由转发请求的命令执行时间超过该配置值时，Hystrix会将执行该命令标记为 TIMEOUT 并抛出异常

hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=5000

```
{
  "timestamp": 1500969665898,
  "status": 500,
  "error": "Internal Server Error",
  "exception": "com.netflix.zuul.exception.ZuulException",
  "message": "timeout"
}
```

路由转发请求时，创建请求连接的超时时间，当小于
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 配置时，
若出现路由连接超时，会自动进行重试路由请求。当大于hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 配置时，
直接按请求命令超时处理，返回 timeout 的错误信息

ribbon.ConnectTimeout=2000

重试失败后的数据
```
{
  "timestamp": 1500969296200,
  "status": 500,
  "error": "Internal Server Error",
  "exception": "com.netflix.zuul.exception.ZuulException",
  "message": "numberof_retries_nextserver_exceeded"
}
```

路由转发请求的超时时间，是对请求建立之后的处理时间,
小于hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 配置时，
会自动重试，大于时不会重试，直接按请求命令超时处理

ribbon.ReadTimeout=5000


###禁用过滤器
zuul.CharacterEncodingFilter.pre.disable=false


事实证明，如果一个服务需要比Hystrix执行超时但是小于ReadTimeout的时间，Zuul会泄漏HTTP连接。总结：
execTime < hystrixTimeout < readTimeout --> OK (no leak)
hystrixTimeout < execTime < readTimeout --> LEAK
hystrixTimeout < readTimeout < execTime --> OK (no leak)
readTimeout < execTime < hystrixTimeout --> OK (no leak)
readTimeout < hystrixTimeout < execTime --> LEAK

zuul.host.socket-timeout-millis=60000
zuul.host.connect-timeout-millis=6000