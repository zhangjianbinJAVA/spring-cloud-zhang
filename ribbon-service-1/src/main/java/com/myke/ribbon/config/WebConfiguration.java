package com.myke.ribbon.config;

import com.myke.ribbon.ext.LoggingRequestInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 15:12
 */
@Configuration
public class WebConfiguration {

    /**
     * 实例化 http客户端
     * <p>
     * 服务消费者的客户端负载均衡功能
     *
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rt = new RestTemplate();

        //set interceptors/requestFactory
        ClientHttpRequestInterceptor ri = new LoggingRequestInterceptor();
        List<ClientHttpRequestInterceptor> ris = new ArrayList<ClientHttpRequestInterceptor>();
        ris.add(ri);
        rt.setInterceptors(ris);
        rt.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        return rt;
    }
}
