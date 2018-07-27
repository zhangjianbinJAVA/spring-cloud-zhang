package com.myke.zuul;

import com.spring4all.swagger.EnableSwagger2Doc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.event.EventListener;

import java.util.Set;

/**
 * @author zhangjianbin
 * @version v1.0
 * @date 2017/5/18 10:26
 */
@Slf4j
@EnableSwagger2Doc
@EnableZuulProxy
@SpringBootApplication
public class ZuulApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulApplication.class).web(true).run(args);
    }

    @EventListener
    public void properties(EnvironmentChangeEvent changeEvent) {
        Set<String> keys = changeEvent.getKeys();
        log.info("keys:{}", keys);

    }
}
