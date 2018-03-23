package com.myke.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * user: zhangjianbin <br/>
 * date: 2018/2/13 11:34
 */
@Slf4j
@Configuration
public class CommonConfig {


    @PostConstruct
    public void run() {
        log.info("加载 service-common依赖");
    }
}
