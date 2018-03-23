###参考
http://blog.springcloud.cn/sc/sc-zuul-excpetion/

### zuul 异常处理
所有请求都必然按照 pre-> route -> post的顺序执行，post返回response，
如果pre,route,post发生错误则执行error，然后再执行post返回response，
但post发生错误后，执行了 error 过滤器，后续没有post过滤器执行，这就造成客户端没有数据返回



###异常处理方案一  由 SendErrorFilter 过滤器处理
```
        try {
            doException();
        } catch (Exception e) {
            log.error("ThrowExcetpionFilter error:{}", e);
            ctx.put(MyZuulConstants.ERROR_STATUS_CODE, 600); //status
            ctx.put(MyZuulConstants.ERROR_EXCEPTION, e);  // Exception 的类型
            ctx.put(MyZuulConstants.ERROR_MESSAGE, e.getMessage()); //自定义的异常信息
        }
```

###异常处理方案二 由 error 类型过滤器处理，对第一种处理方式的补充


### SendErrorFilter 执行条件，上下文中必须包含 error.status_code
```
	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		// only forward to errorPath if it hasn't been forwarded to already
		return ctx.containsKey("error.status_code")
				&& !ctx.getBoolean(SEND_ERROR_FILTER_RAN, false);
	}

```


###不足与优化

外部请求到达 api 网关服务之后，各阶段的过滤器是如何进行调度的？
```
com.netflix.zuul.http.ZuulServlet  service函数

    //zuul 处理外部请求过程时，各个类型过滤器执行逻辑，出现异常都会被 error类型的过
    //滤器处理，error过滤器定义统一的异常处理就是利用这个特性的
    //除 post 阶段的异常之外，都会被 再被 post 过滤器进行处理
    @Override
    public void service(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) throws ServletException, IOException {
        try {
            init((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);

            // Marks this request as having passed through the "Zuul engine", as opposed to servlets
            // explicitly bound in web.xml, for which requests will not have the same data attached
            RequestContext context = RequestContext.getCurrentContext();
            context.setZuulEngineRan();

            try {
                preRoute();
            } catch (ZuulException e) {
                error(e);
                postRoute();
                return;
            }
            try {
                route();
            } catch (ZuulException e) {
                error(e);
                postRoute();
                return;
            }
            try {
                                
                postRoute();
            } catch (ZuulException e) {
                error(e);
                return;
            }

        } catch (Throwable e) {
            error(new ZuulException(e, 500, "UNHANDLED_EXCEPTION_" + e.getClass().getName()));
        } finally {
            RequestContext.getCurrentContext().unset();
        }
    }
   
```

### post 阶段抛出异常，该怎么办？
源码中从 post 过滤器抛出异常，将没有后续的post过滤器处理，则就出现客户端，无返回数据
希望将 post 阶段抛出异常交给 SendErrorFilter 过滤器处理，将异常信息返回给调用者

可以在 error类型过滤器直接组织结果并返回就能实现效果。
但错误信息组织（error 类型）和返回的代码实现（SendErrorFilter）存在多份

####解决方案
定义一个 error 过滤器，让它实现 SendErrorFilter 的功能，这个error过滤器
只对 post 过滤器抛出的异常有效，所以只需要 继承 SendErrorFilter 过滤器，
重写过滤器类型、顺序、执行条件，实现对原有逻辑的得用即可

###怎么判断引起异常的过滤器来处什么阶段？
源码中并没有存储异常来的内容，所以要扩展原来的过滤器处理逻辑，
FilterProcessor.processZuulFilter(ZuulFilter filter) 
当有异常抛出的时候，记录下抛出异常的过滤器

FilterProcessor :zuul 过滤器的核心处理器
- getInstance() 
  
  获取当前处理器的实例
  
- setProcessor(FilterProcessor processor)

  可以使用此方法设置自定义的处理器
  
- processZuulFilter(ZuulFilter filter)
  
  用来执行 filter 的具体逻辑，包括 上下文的设置，判断是否应用执行，执行时一些
  异常的处理

- runFilters(String sType)
  
  根据传入的 filterType 来调用 getFiltersByType(String filterType)获取排序后的过滤器
  列表，然后轮询这些过滤器，并调用  processZuulFilter(zuulFilter) 来依次执行它们
  
  ```
  public Object runFilters(String sType) throws Throwable {
          if (RequestContext.getCurrentContext().debugRouting()) {
              Debug.addRoutingDebug("Invoking {" + sType + "} type filters");
          }
          boolean bResult = false;
          
          //获取排序后的过滤器
          List<ZuulFilter> list = FilterLoader.getInstance().getFiltersByType(sType);
          if (list != null) {
              for (int i = 0; i < list.size(); i++) {
                  ZuulFilter zuulFilter = list.get(i);
                 
                 //执行过滤器
                  Object result = processZuulFilter(zuulFilter);
                  if (result != null && result instanceof Boolean) {
                      bResult |= ((Boolean) result);
                  }
              }
          }
          return bResult;
      }
  ```
     
- getFiltersByType(String filterType)
  
  根据传入的 filterType 获取 api 网关对应类型的过滤器，并根据这些过滤器的 filterOrder
  
  从小到大排序，组织成一个列表返回    
  
  FilterLoader.getInstance().getFiltersByType(sType)
  
- preRoute() :调用所有 pre 类型过滤器
- route()  
- postRoute()
- error()
   
  
  
  


