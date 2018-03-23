### 自定义异常信息

参考SendErrorFilter 过滤器 forward /error端点

###如何定制返回响应结果？
扩展 /error端点的实现

/error 来源与 org.springframework.boot.autoconfigure.web.BasicErrorController类
```
	@RequestMapping
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request,
				isIncludeStackTrace(request, MediaType.ALL));
		HttpStatus status = getStatus(request);
		return new ResponseEntity<Map<String, Object>>(body, status);
	}
```

spring boot 自动化配置机制中，DefaultErrorAttributes作为
ErrorAttributes 接口中的 getErrorAttributes 的默认实现

ErrorMvcAutoConfiguration 自动化配置中
```
    DefaultErrorAttributes 仅在没有 ErrorAttributes 接口的实例时才会被创建
	@Bean
	@ConditionalOnMissingBean(value = ErrorAttributes.class,
		 search = SearchStrategy.CURRENT)
	public DefaultErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes();
	}
```

###自定义 返给客户端的异常属性 
编写一个自定义的实现类，并继承 DefaultErrorAttributes，重写 getErrorAttributes 方法即可

不希望将 timestamp 属性返回给客户端
```
bean ZuulErrorAttributes 将代代替 DefaultErrorAttributes 实例

@Component
public class ZuulErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
            boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
        errorAttributes.remove("timestamp");
        return errorAttributes;
    }
}
```
