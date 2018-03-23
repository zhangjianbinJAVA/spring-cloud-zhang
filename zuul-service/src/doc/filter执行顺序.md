###Zuul 架构图
![Imgage text](..\img\zuul架构.png)

###参考
http://blog.springcloud.cn/sc/sc-fzp-zuul/

在zuul中， 整个请求的过程是这样的，首先将请求给zuulservlet处理，zuulservlet中有一个zuulRunner对象，
该对象中初始化了RequestContext：作为存储整个请求的一些数据，并被所有的zuulfilter共享。
zuulRunner中还有 FilterProcessor，FilterProcessor作为执行所有的zuulfilter的管理器。
FilterProcessor从filterloader 中获取zuulfilter，而zuulfilter是被filterFileManager所加载，
并支持groovy热加载，采用了轮询的方式热加载。有了这些filter之后，zuulservelet首先执行的Pre类型的过滤器，
再执行route类型的过滤器，最后执行的是post 类型的过滤器，
如果在执行这些过滤器有错误的时候则会执行error类型的过滤器。执行完这些过滤器，最终将请求的结果返回给客户端。



###过滤器执行顺序查看
FilterProcessor.runFilters(String sType)函数




###filter 生命周期
![Imgage text](..\img\filter生命周期.png)
###默认filter
![Imgage text](..\img\默认filter.png)
###pre
![Imgage text](..\img\pre-filter.png)
###route
![Imgage text](..\img\route-filter.png)
###post
![Imgage text](..\img\post-filter.png)
