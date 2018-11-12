package com.example.zuul.web;

import de.codecentric.boot.admin.web.servlet.resource.ConcatenatingResourceResolver;
import de.codecentric.boot.admin.web.servlet.resource.PreferMinifiedFilteringResourceResolver;
import de.codecentric.boot.admin.web.servlet.resource.ResourcePatternResolvingResourceResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/25 16:36
 * ================================
 */
@Configuration
public class AdminUIConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[]{"/**"}).addResourceLocations(new String[]{"classpath:/META-INF/spring-boot-admin-server-ui/"}).resourceChain(true).addResolver(new PreferMinifiedFilteringResourceResolver(".min"));
        registry.addResourceHandler(new String[]{"/all-modules.css"}).resourceChain(true).addResolver(new ResourcePatternResolvingResourceResolver(this.resourcePatternResolver, "classpath*:/META-INF/spring-boot-admin-server-ui/*/module.css")).addResolver(new ConcatenatingResourceResolver("\n".getBytes()));
        registry.addResourceHandler(new String[]{"/all-modules.js"}).resourceChain(true).addResolver(new ResourcePatternResolvingResourceResolver(this.resourcePatternResolver, "classpath*:/META-INF/spring-boot-admin-server-ui/*/module.js")).addResolver(new PreferMinifiedFilteringResourceResolver(".min")).addResolver(new ConcatenatingResourceResolver(";\n".getBytes()));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("forward:index.html");
    }
}
