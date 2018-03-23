## eureka 服务端

### 服务注册中心处理，也就是 eureka 服务端对 eureka 客户端 的请求处理
> EurekaServer对于各类REST请求的定义都位于com.netflix.eureka.resources包下

### 服务注册请求
com.netflix.eureka.resources.`ApplicationResource` 类中
```
@POST
    @Consumes({"application/json", "application/xml"})
    public Response addInstance(InstanceInfo info,
                                @HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication) {
        logger.debug("Registering instance {} (replication={})", info.getId(), isReplication);
        // validate that the instanceinfo contains all the necessary required fields
        if (isBlank(info.getId())) {
            return Response.status(400).entity("Missing instanceId").build();
        } else if (isBlank(info.getHostName())) {
            return Response.status(400).entity("Missing hostname").build();
        } else if (isBlank(info.getAppName())) {
            return Response.status(400).entity("Missing appName").build();
        } else if (!appName.equals(info.getAppName())) {
            return Response.status(400).entity("Mismatched appName, expecting " + appName + " but was " + info.getAppName()).build();
        } else if (info.getDataCenterInfo() == null) {
            return Response.status(400).entity("Missing dataCenterInfo").build();
        } else if (info.getDataCenterInfo().getName() == null) {
            return Response.status(400).entity("Missing dataCenterInfo Name").build();
        }

        // handle cases where clients may be registering with bad DataCenterInfo with missing data
        DataCenterInfo dataCenterInfo = info.getDataCenterInfo();
        if (dataCenterInfo instanceof UniqueIdentifier) {
            String dataCenterInfoId = ((UniqueIdentifier) dataCenterInfo).getId();
            if (isBlank(dataCenterInfoId)) {
                boolean experimental = "true".equalsIgnoreCase(serverConfig.getExperimental("registration.validation.dataCenterInfoId"));
                if (experimental) {
                    String entity = "DataCenterInfo of type " + dataCenterInfo.getClass() + " must contain a valid id";
                    return Response.status(400).entity(entity).build();
                } else if (dataCenterInfo instanceof AmazonInfo) {
                    AmazonInfo amazonInfo = (AmazonInfo) dataCenterInfo;
                    String effectiveId = amazonInfo.get(AmazonInfo.MetaDataKey.instanceId);
                    if (effectiveId == null) {
                        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.instanceId.getName(), info.getId());
                    }
                } else {
                    logger.warn("Registering DataCenterInfo of type {} without an appropriate id", dataCenterInfo.getClass());
                }
            }
        }
        
        // 服务进行注册函数
        // 查看 PeerAwareInstanceRegistryImpl的 register()方法 
        registry.register(info, "true".equals(isReplication));
        return Response.status(204).build();  // 204 to be backwards compatible
    }
```

#### PeerAwareInstanceRegistryImpl的 register()方法
> PeerAwareInstanceRegistryImpl的register()方法实现了服务的注册，
并且向其他Eureka Server的Peer节点同步了该注册信息
```
   @Override
    public void register(final InstanceInfo info, final boolean isReplication) {
        int leaseDuration = Lease.DEFAULT_DURATION_IN_SECS;
        if (info.getLeaseInfo() != null && info.getLeaseInfo().getDurationInSecs() > 0) {
            leaseDuration = info.getLeaseInfo().getDurationInSecs();
        }
        //调用 com.netflix.eureka.registry.AbstractInstanceRegistry 中的 register 方法,进行服务注册
        super.register(info, leaseDuration, isReplication);
        replicateToPeers(Action.Register, info.getAppName(), info.getId(), info, null, isReplication);
    }
```

### 注册中心存储结构
com.netflix.eureka.registry.AbstractInstanceRegistry.register :注册实现
注册中心存储了两层map结构
```
private final ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry
            = new ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>>();
          
  if (gMap == null) {
    final ConcurrentHashMap<String, Lease<InstanceInfo>> gNewMap = 
                        new ConcurrentHashMap<String, Lease<InstanceInfo>>();
    gMap = registry.putIfAbsent(registrant.getAppName(), gNewMap);
    if (gMap == null) {
      gMap = gNewMap;
    }
   }       
      
 //第一层的key存储服务名： InstanceInfo 的appName属性          
 Map<String, Lease<InstanceInfo>> gMap = registry.get(registrant.getAppName());
 
 //第二层的key存储：InstanceInfo中的 instanceId属性
   Lease<InstanceInfo> existingLease = gMap.get(registrant.getId());
             
```



##github
Eureka的Github：https://github.com/Netflix/Eureka

##Region、Zone解析
region ：表示AWS中的地理位置，每个region 都有多个 availability zone,各个 region 之间完全隔离;

在非AWS环境下，可以将 availability zone 理解成机房，将 region 理解为跨机房的 eureka集群;

Spring Cloud中默认的region是us-east-1

http://www.itmuch.com/spring-cloud-1/

对region和zone感兴趣的读者可前往http://blog.csdn.net/awschina/article/details/17639191 扩展阅读。


##Eureka包含两个组件：Eureka Server 和 Eureka Client，它们的作用如下
- Eureka Client是一个Java客户端，用于简化与Eureka Server的交互；

- Eureka Server提供服务发现的能力，各个微服务启动时，会通过Eureka Client向Eureka Server进行注册自己的信息（例如网络信息），Eureka Server会存储该服务的信息；

- 微服务启动后，会周期性地向Eureka Server发送心跳（默认周期为30秒）以续约自己的信息。

- 如果Eureka Server在一定时间内没有接收到某个微服务节点的心跳，Eureka Server将会注销该微服务节点（默认90秒）；

- 每个Eureka Server同时也是Eureka Client，多个Eureka Server之间通过复制的方式完成服务注册表的同步；
Eureka Client会缓存Eureka Server中的信息。即使所有的Eureka Server节点都宕掉，服务消费者依然可以使用缓存中的信息找到服务提供者。


# 参考资料
http://fengyilin.iteye.com/blog/2367265

## 为什么利用Eureka注册一个instance需要很长的时间 
一个新注册的instance在和别的instance通信之前可能需要2min的间隔。

##Eureka server 
* Eureka server还有一种peer-aware模式，可以和其他的Eureka server相互复制注册信息，从而对外提供负载均衡和弹性（即服务的持续可用性）。
在默认情况下Eureka server就工作在Peer-aware模式下，所以一个Eureka server也是一个Eureka client，并把自己的信息注册到一个peer中。
这也是在生产环境下Eureka的常用模式，在单机模式下，如在测试或者为了了解原理时，可以将registerWithEureka设置成false。 

* 当一个Eureka server启动时，它将尝试从其他的peer 节点中获取注册信息。对于每一个peer节点，这个操作最多会尝试5次（通过eureka.server.numberRegistrySyncRetries配置），如果这个操作失败，这个server会在5min内（通过eureka.server.getWaitTimeInMsWhenSyncEmpty配置）阻止其他客户端从自己这里获取注册信息。 

* Eureka peer-aware模式引入了“self-preservation”的概念，这也将Eureka的复杂度提升了一个级别（可以通过将eureka.server.enableSelfPreservation设置成false来关闭此功能）

以下摘自Netflix的描述 
>  当一个Eureka server 启动，它将尝试从一个相邻节点中获取所有的注册信息，如果从一个邻接点中获取信息出现问题，此server会尝试从其他节点获取，直到所有节点都失败。如果这个服务成功的从其他节点中获取到了注册信息，它将基于这些信息设置一个刷新阈值（心跳阈值）。如果某个时刻，心跳数低于这个阈值的一个百分比，这个服务将停止移除instance，从而保护当前已经注册过的instance的信息，即进入self-preservation模式。 


## eureka 常见问题 
http://www.itmuch.com/spring-cloud-sum-eureka/

## eureka 自我保护详解
http://fanlychie.github.io/post/spring-cloud-netflix-eureka.html?utm_source=tuicool&utm_medium=referral

##深入理解Eureka之源码解析
- http://blog.csdn.net/forezp/article/details/73017664
- http://blog.abhijitsarkar.org/technical/netflix-eureka/
- http://docs.aws.amazon.com/zh_cn/AWSEC2/latest/UserGuide/using-regions-availability-zones.html
- http://nobodyiam.com/2016/06/25/dive-into-eureka/


