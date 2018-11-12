package com.myke.feign.config.env;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/26 14:59
 * ================================
 */
@Configuration
public class MyConfigBootstrapConfiguration {

    @Bean
    public MyPropertySourceLocator myPropertySourceLocator() {
        return new MyPropertySourceLocator();
    }
}
