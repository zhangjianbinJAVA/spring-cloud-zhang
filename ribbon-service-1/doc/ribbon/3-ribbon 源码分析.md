##深入理解Ribbon
在Riibon中一个非常重要的组件为LoadBalancerClient，它作为负载均衡的一个客户端。它在spring-cloud-commons包下
的LoadBalancerClient是一个接口它的实现类是RibbonLoadBalancerClient这个类是非常重要的一个类，最终的负载均衡的请求处理，由它来执行.

其中LoadBalancerClient接口，有如下三个方法：
```
public interface LoadBalancerClient extends ServiceInstanceChooser {
  #根据serviceId来获取ServiceInstance
  ServiceInstance choose(String serviceId);
  #excute()为执行请求
  <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException;
  #重构url
  URI reconstructURI(ServiceInstance instance, URI original);
}
```

创建客户端、负载平衡器和客户端配置实例的工厂。它为每个客户端创建一个Spring ApplicationContext，并提取它的bean需要从这里。
```
例：根据服务名ID，获取 RibbonLoadBalancerContext
org.springframework.cloud.netflix.ribbon.SpringClientFactory
```

在RibbonLoadBalancerClient的源码中，其中choose()方法是选择具体服务实例的一个方法。该方法通过getServer()方法去获取实例，
经过源码跟踪，最终交给了ILoadBalancer类去选择服务实例。
 ```
 org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient
 
 #LoadBalancerClient接口的reconstructURI函数
 @Override
 	public URI reconstructURI(ServiceInstance instance, URI original) {
 		Assert.notNull(instance, "instance can not be null");
 		String serviceId = instance.getServiceId();
 		
 		//SpringClientFactory.clientFactory 对象中获取对应 serviceId的负载均衡器的上下文
 		//RibbonLoadBalancerContext 对象
 		RibbonLoadBalancerContext context = this.clientFactory
 				.getLoadBalancerContext(serviceId);
 		
 		//构建具体服务实例信息的 server对象		
 		Server server = new Server(instance.getHost(), instance.getPort());
 		
 		boolean secure = isSecure(server, serviceId);
 		URI uri = original;
 		if (secure) {
 			uri = UriComponentsBuilder.fromUri(uri).scheme("https").build().toUri();
 		}
 		
 		//构建服务实例的URL
 		return context.reconstructURIWithServer(server, uri);
 		
 		SpringClientFactory :一个用来创建客户端负载均衡器的工厂类，该工厂类会为每一个不同名的 
 		ribbon 客户端生成不同的 spring 上下文
 		
 		RibbonLoadBalancerContext：是LoadBalancerContext的子类，该类用于存储一些被负载均衡器使用的上下文
 		内容和 api操作 （reconstructURIWithServer就是其中之一）
 		
 		
 		
 	}
 
    #选择服务实例
 	@Override
 	public ServiceInstance choose(String serviceId) {
 		Server server = getServer(serviceId);
 		if (server == null) {
 			return null;
 		}
 		return new RibbonServer(serviceId, server, isSecure(server, serviceId),
 				serverIntrospector(serviceId).getMetadata(server));
 	}
 
    #执行请求
 	@Override
 	public <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException {
 		#选择负载均衡器
 		ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
 		
 		#获得具体的服务实例 server 	
 		Server server = getServer(loadBalancer);
 		if (server == null) {
 			throw new IllegalStateException("No instances available for " + serviceId);
 		}
 		将 server 包装成了 RibbonServer对象，包含 服务id、服务信息、服务的metadata元数据 	
 		RibbonServer对象 是ServiceInstance接口的实现	
 		RibbonServer ribbonServer = new RibbonServer(serviceId, server, isSecure(server,
 				serviceId), serverIntrospector(serviceId).getMetadata(server));
 
 		RibbonLoadBalancerContext context = this.clientFactory
 				.getLoadBalancerContext(serviceId);
 				
 		//对服务的请求进行了跟踪记录		
 		RibbonStatsRecorder statsRecorder = new RibbonStatsRecorder(context, server);
 
 		try {
 		
 		    回调LoadBalancerInterceptor请求拦截器中LoadBalancerRequest的apply函数，
 		    向一个实际具体服务实例发起请求，从而实现以服务名为host的URL请求到 host:port形式
 		    访问地址的转换
 			T returnVal = request.apply(ribbonServer);
 			statsRecorder.recordStats(returnVal);
 			return returnVal;
 		}
 		// catch IOException and rethrow so RestTemplate behaves correctly
 		catch (IOException ex) {
 			statsRecorder.recordStats(ex);
 			throw ex;
 		}
 		catch (Exception ex) {
 			statsRecorder.recordStats(ex);
 			ReflectionUtils.rethrowRuntimeException(ex);
 		}
 		return null;
 	}
 ```

```
选择负载均衡器 分析
org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient.getLoadBalancer

protected ILoadBalancer getLoadBalancer(String serviceId) {
		return this.clientFactory.getLoadBalancer(serviceId);
	}
	

#过程分析
package org.springframework.beans.factory;
 //分层工厂
public interface HierarchicalBeanFactory extends BeanFactory {
     //返回工厂的父工厂
    BeanFactory getParentBeanFactory();
     //在当前工厂中寻找bean 忽略继承来工厂里bean
    boolean containsLocalBean(String name);
}	

	/**
	 * 根据服务名 获取 ILoadBalancer
	 * Get the load balancer associated with the name.
	 * @throws RuntimeException if any error occurs
	 */
	public ILoadBalancer getLoadBalancer(String name) {
		return getInstance(name, ILoadBalancer.class);
	}
	
    org.springframework.cloud.netflix.ribbon.SpringClientFactory.getInstance
    public <C> C getInstance(String name, Class<C> type) {
            C instance = super.getInstance(name, type);
            if (instance != null) {
                return instance;
            }
            IClientConfig config = getInstance(name, IClientConfig.class);
            return instantiateWithConfig(getContext(name), type, config);
        }
	
  instance 结果：com.netflix.loadbalancer.ZoneAwareLoadBalancer ，ribbon 默认的负载均衡器
  
  BaseLoadBalancer类实现了基础的负载均衡，而DynamicServerListLoadBalancer和ZoneAwareLoadBalancer在负载均衡的策略上做了
  一些功能的扩展。
  那么在整合Ribbon的时候Spring Cloud默认采用了哪个具体实现呢？我们通过RibbonClientConfiguration配置类，
  可以知道在整合时默认采用了ZoneAwareLoadBalancer来实现负载均衡器。
  @Bean
  @ConditionalOnMissingBean
  public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
          ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
          IRule rule, IPing ping) {
      ZoneAwareLoadBalancer<Server> balancer = LoadBalancerBuilder.newBuilder()
              .withClientConfig(config).withRule(rule).withPing(ping)
              .withServerListFilter(serverListFilter).withDynamicServerList(serverList)
              .buildDynamicServerListLoadBalancer();
      return balancer;
  }
  
    由上面可以 传入的 loadBalancer 为ZoneAwareLoadBalancer    
    获得具体的服务实例 分析
  	protected Server getServer(ILoadBalancer loadBalancer) {
  		if (loadBalancer == null) {
  			return null;
  		}
  		通过 ZoneAwareLoadBalancer的chooseServer 函数获取了
  		负载均衡策略分配到的服务实例对象 server
  		return loadBalancer.chooseServer("default"); // TODO: better handling of key
  	}
  	

```

ILoadBalancer在ribbon-loadbalancer的jar包下,它是定义了实现软件负载均衡的一个接口，
它需要一组可供选择的服务注册列表信息，以及根据特定方法去选择服务，它的源码如下 ：
```
public interface ILoadBalancer {

    public void addServers(List<Server> newServers);
    public Server chooseServer(Object key);
    public void markServerDown(Server server);
    public List<Server> getReachableServers();
    public List<Server> getAllServers();
}
```
- addServers()方法是向负载均衡器中维护的实例列表增加服务实例；
- chooseServer()方法是通过某种策略，从负载均衡器中挑选出一个具体的服务实例；
- markServerDown()方法用来标记某个服务下线；
- getReachableServers()获取可用的Server集合；
- getAllServers()获取所有的Server集合。包含可用的和不可用的服务实例

可用发现，配置以下信息，IClientConfig、IRule、IPing、ServerList、ServerListFilter和ILoadBalancer，
查看BaseLoadBalancer类，它默认的情况下，实现了以下配置：
- IClientConfig ribbonClientConfig: DefaultClientConfigImpl配置
- IRule ribbonRule: RoundRobinRule 路由策略
- IPing ribbonPing: DummyPing
- ServerList ribbonServerList: ConfigurationBasedServerList
- ServerListFilter ribbonServerListFilter: ZonePreferenceServerListFilter
- ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer

IClientConfig 用于对客户端或者负载均衡的配置，它的默认实现类为DefaultClientConfigImpl。

IRule用于复杂均衡的策略，它有三个方法，其中choose()是根据key 来获取server,setLoadBalancer()
和getLoadBalancer()是用来设置和获取ILoadBalancer的，它的源码如下：
```
public interface IRule{

    public Server choose(Object key);

    public void setLoadBalancer(ILoadBalancer lb);

    public ILoadBalancer getLoadBalancer();    
}
```

IRule有很多默认的实现类，这些实现类根据不同的算法和逻辑来处理负载均衡。Ribbon实现的IRule有以下。在大多数情况下，这些默认的实现类
是可以满足需求的，如果有特性的需求，可以自己实现。
- BestAvailableRule 选择最小请求数
- ClientConfigEnabledRoundRobinRule 轮询
- RandomRule 随机选择一个server
- RoundRobinRule 轮询选择server
- RetryRule 根据轮询的方式重试
- WeightedResponseTimeRule 根据响应时间去分配一个weight ，weight越低，被选择的可能性就越低
- ZoneAvoidanceRule 根据server的zone区域和可用性来轮询选择

IPing是用来想server发生”ping”，来判断该server是否有响应，从而判断该server是否可用。它有一个isAlive()方法，它的源码如下：
```
public interface IPing {
    public boolean isAlive(Server server);
}
```

IPing的实现类有PingUrl、PingConstant、NoOpPing、DummyPing和NIWSDiscoveryPing
- PingUrl 真实的去ping 某个url，判断其是否alive
- PingConstant 固定返回某服务是否可用，默认返回true，即可用
- NoOpPing 不去ping,直接返回true,即可用。
- DummyPing 直接返回true，并实现了initWithNiwsConfig方法。
- NIWSDiscoveryPing，根据DiscoveryEnabledServer的InstanceInfo的InstanceStatus去判断，如果为InstanceStatus.UP，则为可用，
否则不可用。

ServerList是定义获取所有的server的注册列表信息的接口，它的代码如下：
```
public interface ServerList<T extends Server> {

    public List<T> getInitialListOfServers();
    public List<T> getUpdatedListOfServers();   

}
```

ServerListFilter接口，定了可根据配置去过滤或者根据特性动态获取符合条件的server列表的方法，代码如下：
```
public interface ServerListFilter<T extends Server> {

    public List<T> getFilteredListOfServers(List<T> servers);

}

```
阅读DynamicServerListLoadBalancer的源码，DynamicServerListLoadBalancer的构造函数中有个initWithNiwsConfig()方法。
在该方法中，经过一系列的初始化配置，最终执行了restOfInit()方法。
```
    public DynamicServerListLoadBalancer(IClientConfig clientConfig) {
        initWithNiwsConfig(clientConfig);
    }
    
    //与此类配置初始化
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        try {
            super.initWithNiwsConfig(clientConfig);
            String niwsServerListClassName = clientConfig.getPropertyAsString(
                    CommonClientConfigKey.NIWSServerListClassName,
                    DefaultClientConfigImpl.DEFAULT_SEVER_LIST_CLASS);

            ServerList<T> niwsServerListImpl = (ServerList<T>) ClientFactory
                    .instantiateInstanceWithClientConfig(niwsServerListClassName, clientConfig);
            this.serverListImpl = niwsServerListImpl;

            if (niwsServerListImpl instanceof AbstractServerList) {
                AbstractServerListFilter<T> niwsFilter = ((AbstractServerList) niwsServerListImpl)
                        .getFilterImpl(clientConfig);
                niwsFilter.setLoadBalancerStats(getLoadBalancerStats());
                this.filter = niwsFilter;
            }

            String serverListUpdaterClassName = clientConfig.getPropertyAsString(
                    CommonClientConfigKey.ServerListUpdaterClassName,
                    DefaultClientConfigImpl.DEFAULT_SERVER_LIST_UPDATER_CLASS
            );

            this.serverListUpdater = (ServerListUpdater) ClientFactory
                    .instantiateInstanceWithClientConfig(serverListUpdaterClassName, clientConfig);

            restOfInit(clientConfig);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception while initializing NIWSDiscoveryLoadBalancer:"
                            + clientConfig.getClientName()
                            + ", niwsClientConfig:" + clientConfig, e);
        }
    }
```

在restOfInit()方法上，有一个 updateListOfServers()的方法，该方法是用来获取所有的ServerList的。
```
  void restOfInit(IClientConfig clientConfig) {
        boolean primeConnection = this.isEnablePrimingConnections();
        // turn this off to avoid duplicated asynchronous priming done in BaseLoadBalancer.setServerList()
        this.setEnablePrimingConnections(false);
        enableAndInitLearnNewServersFeature();

        //该方法是用来获取所有的ServerList
        updateListOfServers();
        if (primeConnection && this.getPrimeConnections() != null) {
            this.getPrimeConnections()
                    .primeConnections(getReachableServers());
        }
        this.setEnablePrimingConnections(primeConnection);
        LOGGER.info("DynamicServerListLoadBalancer for client {} initialized: {}",
         clientConfig.getClientName(), this.toString());
    }
```

进一步跟踪updateListOfServers()方法的源码，最终由serverListImpl.getUpdatedListOfServers()获取所有的服务列表的，代码如下：
```
@VisibleForTesting
    public void updateListOfServers() {
        List<T> servers = new ArrayList<T>();
        if (serverListImpl != null) {
            //获取所有的服务列表的
            servers = serverListImpl.getUpdatedListOfServers();
            
            LOGGER.debug("List of Servers for {} obtained from Discovery client: {}",
                    getIdentifier(), servers);

            if (filter != null) {
                servers = filter.getFilteredListOfServers(servers);
                LOGGER.debug("Filtered List of Servers for {} obtained from Discovery client: {}",
                        getIdentifier(), servers);
            }
        }
        updateAllServerList(servers);
    }
```

而serverListImpl是ServerList接口的具体实现类。跟踪代码，ServerList的实现类为DiscoveryEnabledNIWSServerList，
在ribbon-eureka.jar的com.netflix.niws.loadbalancer下。其中DiscoveryEnabledNIWSServerList有 getInitialListOfServers()
和getUpdatedListOfServers()方法，具体代码如下：
```
com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList

@Override
    public List<DiscoveryEnabledServer> getInitialListOfServers(){
        return obtainServersViaDiscovery();
    }

    @Override
    public List<DiscoveryEnabledServer> getUpdatedListOfServers(){
        return obtainServersViaDiscovery();
    }
```
继续跟踪源码，obtainServersViaDiscovery（），是根据eurekaClientProvider.get()来回去EurekaClient，再根据EurekaClient来获取
注册列表信息，代码如下：
```
{
        List<DiscoveryEnabledServer> serverList = new ArrayList<DiscoveryEnabledServer>();

        if (eurekaClientProvider == null || eurekaClientProvider.get() == null) {
            logger.warn("EurekaClient has not been initialized yet, returning an empty list");
            return new ArrayList<DiscoveryEnabledServer>();
        }

        //根据eurekaClientProvider.get()来回获取去EurekaClient，再根据EurekaClient来获取注册列表信息
        EurekaClient eurekaClient = eurekaClientProvider.get();
        
        if (vipAddresses!=null){
            for (String vipAddress : vipAddresses.split(",")) {
                // if targetRegion is null, it will be interpreted as the same region of client
                List<InstanceInfo> listOfInstanceInfo = 
                eurekaClient.getInstancesByVipAddress(vipAddress, isSecure, targetRegion);
                for (InstanceInfo ii : listOfInstanceInfo) {
                    if (ii.getStatus().equals(InstanceStatus.UP)) {

                        if(shouldUseOverridePort){
                            if(logger.isDebugEnabled()){
                                logger.debug("Overriding port on client name: " + clientName + " to " + overridePort);
                            }

                            // copy is necessary since the InstanceInfo builder just uses the original reference,
                            // and we don't want to corrupt the global eureka copy of the object which may be
                            // used by other clients in our system
                            InstanceInfo copy = new InstanceInfo(ii);

                            if(isSecure){
                                ii = new InstanceInfo.Builder(copy).setSecurePort(overridePort).build();
                            }else{
                                ii = new InstanceInfo.Builder(copy).setPort(overridePort).build();
                            }
                        }

                        DiscoveryEnabledServer des = new DiscoveryEnabledServer(ii, isSecure, shouldUseIpAddr);
                        des.setZone(DiscoveryClient.getZone(ii));
                        serverList.add(des);
                    }
                }
                if (serverList.size()>0 && prioritizeVipAddressBasedServers){
                    break; // if the current vipAddress has servers, we dont use subsequent vipAddress based servers
                }
            }
        }
        return serverList;
    }
```
其中eurekaClientProvider的实现类是LegacyEurekaClientProvider，它是一个获取eurekaClient类，通过静态的方法去获取eurekaClient，
其代码如下：
```
class LegacyEurekaClientProvider implements Provider<EurekaClient> {

    private volatile EurekaClient eurekaClient;

    @Override
    public synchronized EurekaClient get() {
        if (eurekaClient == null) {
            eurekaClient = DiscoveryManager.getInstance().getDiscoveryClient();
        }

        return eurekaClient;
    }
}
```

EurekaClient的实现类为DiscoveryClient，在之前已经分析了它具有服务注册、获取服务注册列表等的全部功能。

####由此可见，负载均衡器是从EurekaClient获取服务信息，并根据IRule去路由，并且根据IPing去判断服务的可用性。

那么现在还有个问题，负载均衡器多久一次去获取一次从Eureka Client获取注册信息呢。

在BaseLoadBalancer类下，BaseLoadBalancer的构造函数，该构造函数开启了一个PingTask任务，代码如下：
```
 public BaseLoadBalancer(String name, IRule rule, LoadBalancerStats stats,
            IPing ping, IPingStrategy pingStrategy) {
        if (logger.isDebugEnabled()) {
            logger.debug("LoadBalancer:  initialized");
        }
        this.name = name;
        this.ping = ping;
        this.pingStrategy = pingStrategy;
        setRule(rule);
        //ping 任务
        setupPingTask();
        lbStats = stats;
        init();
    }

```
setupPingTask()的具体代码逻辑，它开启了ShutdownEnabledTimer执行PingTask任务，在默认情况下pingIntervalSeconds为10，即每10秒钟，
想EurekaClient发送一次”ping”。
```
 void setupPingTask() {
        if (canSkipPing()) {
            return;
        }
        if (lbTimer != null) {
            lbTimer.cancel();
        }
        lbTimer = new ShutdownEnabledTimer("NFLoadBalancer-PingTimer-" + name,
                true);
                
                        //ping任务    
        lbTimer.schedule(new PingTask(), 0, pingIntervalSeconds * 1000);
        forceQuickPing();
    }
```
PingTask源码，即new一个Pinger对象，并执行runPinger()方法。
```
com.netflix.loadbalancer.BaseLoadBalancer.PingTask

class PingTask extends TimerTask {
        public void run() {
            try {
                new Pinger(pingStrategy).runPinger();
            } catch (Exception e) {
                logger.error("LoadBalancer [{}]: Error pinging", name, e);
            }
        }
    }
```
查看Pinger的runPinger()方法，最终根据 pingerStrategy.pingServers(ping, allServers)来获取服务的可用性，如果该返回结果，
如之前相同，则不去向EurekaClient获取注册列表，如果不同则通知ServerStatusChangeListener或者changeListeners发生了改变，
进行更新或者重新拉取。
```
public void runPinger() {
            if (pingInProgress.get()) {
                return; // Ping in progress - nothing to do
            } else {
                pingInProgress.set(true);
            }
            // we are "in" - we get to Ping

            Server[] allServers = null;
            boolean[] results = null;

            Lock allLock = null;
            Lock upLock = null;

            try {
                /*
                 * The readLock should be free unless an addServer operation is
                 * going on...
                 */
                allLock = allServerLock.readLock();
                allLock.lock();
                allServers = allServerList.toArray(new Server[allServerList.size()]);
                allLock.unlock();

                int numCandidates = allServers.length;
                results = pingerStrategy.pingServers(ping, allServers);

                final List<Server> newUpList = new ArrayList<Server>();
                final List<Server> changedServers = new ArrayList<Server>();

                for (int i = 0; i < numCandidates; i++) {
                    boolean isAlive = results[i];
                    Server svr = allServers[i];
                    boolean oldIsAlive = svr.isAlive();

                    svr.setAlive(isAlive);

                    if (oldIsAlive != isAlive) {
                        changedServers.add(svr);
                        if (logger.isDebugEnabled()) {
                            logger.debug("LoadBalancer:  Server [" + svr.getId()
                                    + "] status changed to "
                                    + (isAlive ? "ALIVE" : "DEAD"));
                        }
                    }

                    if (isAlive) {
                        newUpList.add(svr);
                    }
                }
                upLock = upServerLock.writeLock();
                upLock.lock();
                upServerList = newUpList;
                upLock.unlock();

                notifyServerStatusChangeListener(changedServers);

            } catch (Throwable t) {
                logger.error("Throwable caught while running the Pinger-"
                        + name, t);
            } finally {
                pingInProgress.set(false);
            }
        }
    }
```


由此可见，LoadBalancerClient是在初始化的时候，会向Eureka回去服务注册列表，并且向通过10s一次向EurekaClient发送“ping”，
来判断服务的可用性，如果服务的可用性发生了改变或者服务数量和之前的不一致，则更新或者重新拉取。LoadBalancerClient有了这些服务注册列表，
就可以根据具体的IRule来进行负载均衡。

###RestTemplate是如何和Ribbon结合的
``` 
@LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
```
@LoadBalanced有哪些类用到了LoadBalanced有哪些类用到了， 发现LoadBalancerAutoConfiguration类，
即LoadBalancer自动配置类。ribbon 实现的负载均衡自动化配置
 ```
 @Configuration
 @ConditionalOnClass(RestTemplate.class)
 @ConditionalOnBean(LoadBalancerClient.class)
 public class LoadBalancerAutoConfiguration {
 
    //维护被 @LoadBalanced 注解修饰的 RestTemplate对象列表
 	@LoadBalanced
 	@Autowired(required = false)
 	private List<RestTemplate> restTemplates = Collections.emptyList();
 
 	@Bean
 	public SmartInitializingSingleton loadBalancedRestTemplateInitializer(
 			final List<RestTemplateCustomizer> customizers) {
 		return new SmartInitializingSingleton() {
 			@Override
 			public void afterSingletonsInstantiated() {
 				for (RestTemplate restTemplate : LoadBalancerAutoConfiguration.this.restTemplates) {
 					for (RestTemplateCustomizer customizer : customizers) {
 						customizer.customize(restTemplate);
 					}
 				}
 			}
 		};
 	}
 
    //用于给 RestTemplate 增加 LoadBalancerInterceptor 拦截器
 	@Bean
 	@ConditionalOnMissingBean
 	public RestTemplateCustomizer restTemplateCustomizer(
 			final LoadBalancerInterceptor loadBalancerInterceptor) {
 		return new RestTemplateCustomizer() {
 			@Override
 			public void customize(RestTemplate restTemplate) {
 				List<ClientHttpRequestInterceptor> list = new ArrayList<>(
 						restTemplate.getInterceptors());
 				list.add(loadBalancerInterceptor);
 				restTemplate.setInterceptors(list);
 			}
 		};
 	}
 
    //用于实现对客户端发起请求时进行拦截，以实现客户端负载均衡
 	@Bean
 	public LoadBalancerInterceptor ribbonInterceptor(
 			LoadBalancerClient loadBalancerClient) {
 		return new LoadBalancerInterceptor(loadBalancerClient);
 	}
 
 }
 ```
在该类中，首先维护了一个被@LoadBalanced修饰的RestTemplate对象的List，在初始化的过程中，通过调用customizer.customize(restTemplate)
方法来给RestTemplate增加拦截器LoadBalancerInterceptor。

而LoadBalancerInterceptor，用于实时拦截，在LoadBalancerInterceptor这里实现来负载均衡。LoadBalancerInterceptor的拦截方法如下：
```
org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor

    //RestTemplate 发起请求时会被 intercept 函数所拦截
    @Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {
		
		final URI originalUri = request.getURI();
		
		//获取服务名
		String serviceName = originalUri.getHost();
		
		//execute函数根据服务名选择实例并发起实际的请求
		//LoadBalancerClient是一个抽象的负载均衡器接口，
		//具体实现为org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient
		return this.loadBalancer.execute(serviceName,
				new LoadBalancerRequest<ClientHttpResponse>() {
                    //apply为回调函数
                    在 RibbonLoadBalancerClient.execute中调用
                    ServiceInstance为服务实例的抽象定义,
                    RibbonServer对象 是ServiceInstance接口的实现	                    
					@Override
					public ClientHttpResponse apply(final ServiceInstance instance)
							throws Exception {
							
						接收到具体的 ServiceInstance 后，如何组织请求地址呢？
						通过负载均衡器 LoadBalancerClient接口的reconstructURI函数
						来重新构建一个url来进行访问
						HttpRequest serviceRequest = new ServiceRequestWrapper(request,
								instance);
						
						调用 InterceptingClientHttpRequest.InterceptingRequestExecution.execute			
						return execution.execute(serviceRequest, body);
					}

				});
	}
	
通过	LoadBalancerInterceptor拦截器对 RestTemplate的请求进行拦截，并利用 spring cloud 的负载均衡器
LoadBalancerClient将逻辑服务名为 host的 url 转换成具体的服务实例地址的过程，在使用ribbon实现负载均衡器
的时候，实际使用的还是ribbon定义的 ILoadBalancer接口的实现，自动化采用 ZoneAwareLoadBalancer的实例来实现
客户端负载均衡。

```
```
InterceptingClientHttpRequest.InterceptingRequestExecution.execute

public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
            if(this.iterator.hasNext()) {
                ClientHttpRequestInterceptor delegate1 = (ClientHttpRequestInterceptor)this.iterator.next();
                return delegate1.intercept(request, body, this);
            } else {
                //创建请求
               request.getURI()为 LoadBalancerClient接口的reconstructURI函数
                ClientHttpRequest delegate = InterceptingClientHttpRequest.this.
                            requestFactory.createRequest(request.getURI(), request.getMethod());
                            
                delegate.getHeaders().putAll(request.getHeaders());
                if(body.length > 0) {
                    StreamUtils.copy(body, delegate.getBody());
                }

                return delegate.execute();
            }
        }
```


###总结
综上所述，Ribbon的负载均衡，主要通过LoadBalancerClient来实现的，而LoadBalancerClient具体交给了
ILoadBalancer来处理，ILoadBalancer通过配置IRule、IPing等信息，并向EurekaClient获取注册列表的信息，
并默认10秒一次向EurekaClient发送“ping”,进而检查是否更新服务列表，最后，得到注册列表后，
ILoadBalancer根据IRule的策略进行负载均衡。

而RestTemplate 被@LoadBalance注解后，能过用负载均衡，主要是维护了一个被@LoadBalance注解的
RestTemplate列表，并给列表中的RestTemplate添加拦截器，进而交给负载均衡器去处理

分析开始到结束
- @LoadBalanced----------->>LoadBalancerClient
- LoadBalancerClient------>> LoadBalancerAutoConfiguration
- LoadBalancerInterceptor --->> RibbonLoadBalancerClient
