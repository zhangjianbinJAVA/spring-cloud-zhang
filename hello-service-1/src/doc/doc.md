如果你的工程的是独立的，没有父模块，使用maven的命令package打包即可
如果是多模块的工程，而你只是打某个模块的jar包，使用package -pl 模块名 -am打包即可


pom.xml配置spring boot插件
```
<build>  
    <plugins>  
        <plugin>  
            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-maven-plugin</artifactId>  
            <version>${spring-boot.version}</version>  
            <executions>  
                <execution>  
                    <goals>  
                        <goal>repackage</goal>  
                    </goals>  
                </execution>  
            </executions>  
        </plugin>  
    </plugins>  
</build>  
```
启动时报错：

1、xxx.jar中没有主清单属性

原因是漏了
```
<executions>  
    <execution>  
        <goals>  
            <goal>repackage</goal>  
        </goals>  
    </execution>  
</executions>  
```

###springboot文件上传
```
@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(1048576); //1Mb
        multipartResolver.setMaxInMemorySize(1048576);
        multipartResolver.setMaxUploadSize(1048576);
        return multipartResolver;
    }
}
```

###参考
https://docs.spring.io/spring-boot/docs/current/maven-plugin/repackage-mojo.html



#### HandlerMethodArgumentResolver 自定义controller参数解析器绑定数据
http://www.cnblogs.com/softidea/p/5886560.html
```
public class MyArgumentsResolver implements HandlerMethodArgumentResolver {
    /**
     * 解析器是否支持当前参数
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 指定参数如果被应用MyParam注解，则使用该解析器。
        // 如果直接返回true，则代表将此解析器用于所有参数
        return parameter.hasParameterAnnotation(MyParam.class);
    }

    /**
     * 将request中的请求参数解析到当前Controller参数上
     * @param parameter 需要被解析的Controller参数，此参数必须首先传给{@link #supportsParameter}并返回true
     * @param mavContainer 当前request的ModelAndViewContainer
     * @param webRequest 当前request
     * @param binderFactory 生成{@link WebDataBinder}实例的工厂
     * @return 解析后的Controller参数
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        
        return null;
    }
}
```


####实现一个解析原始类型的参数解析器
对于如何解析原始类型参数，SpringMVC已经有了一个内置的实现——RequestParamMethodArgumentResolver，
因此完全可以参考这个实现来自定义我们自己的解析器。

如上所述，解析器逻辑的主要部分都在resolveArgument方法内，这里就说说自定义该方法的实现。
```
@Override
public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                              NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    // 解析器中的自定义逻辑——urldecode
    Object arg = URLDecoder.decode(webRequest.getParameter(parameter.getParameterName()), "UTF-8");

    // 将解析后的值绑定到对应的Controller参数上，利用DataBinder提供的方法便捷的实现类型转换
    if (binderFactory != null) {
        
        // 生成参数绑定器，第一个参数为request请求对象，第二个参数为需要绑定的目标对象，第三个参数为需要绑定的目标对象名
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, parameter.getParameterName());
        
        try {

            // 将参数转到预期类型，第一个参数为解析后的值，第二个参数为绑定Controller参数的类型，第三个参数为绑定的Controller参数
            arg = binder.convertIfNecessary(arg, parameter.getParameterType(), parameter);
        
        } catch (ConversionNotSupportedException ex) {
            throw new MethodArgumentConversionNotSupportedException(arg, ex.getRequiredType(),
                    parameter.getParameterName(), parameter, ex.getCause());
        } catch (TypeMismatchException ex) {
            throw new MethodArgumentTypeMismatchException(arg, ex.getRequiredType(),
                    parameter.getParameterName(), parameter, ex.getCause());
        }
    }
    return arg;
}
```


####这个DefaultErrorAttributes有什么用呢？主要有两个作用
1. 实现了ErrorAttributes接口，具备提供Error Attributes的能力，当处理/error错误页面时，需要从该bean中读取错误信息以供返回；
2. 实现了HandlerExceptionResolver接口并具有最高优先级，即DispatcherServlet在doDispatch过程中有异常抛出时，先由DefaultErrorAttributes处理。从下面代码中可以发现，DefaultErrorAttributes在处理过程中，是讲ErrorAttributes保存到了request中。事实上，这是DefaultErrorAttributes能够在后面返回Error
   Attributes的原因，实现HandlerExceptionResolver接口，是DefaultErrorAttributes实现ErrorAttributes接口的手段。