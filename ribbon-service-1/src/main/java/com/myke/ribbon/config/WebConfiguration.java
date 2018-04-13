package com.myke.ribbon.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 15:12
 */
@Configuration
public class WebConfiguration {

    @LoadBalanced
    @Bean
    public RestTemplate hystrixRestTemplate() {
        return new RestTemplate();
    }

    /**
     * 实例化 http客户端
     * <p>
     * 服务消费者的客户端负载均衡功能
     *
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate retryRestTemplate() {
        // Ribbon+RestTemplate的重试
        SimpleClientHttpRequestFactory httpComponentsClientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setConnectTimeout(1000);
        httpComponentsClientHttpRequestFactory.setReadTimeout(1000);
        RestTemplate rt = new RestTemplate(httpComponentsClientHttpRequestFactory);

//        RestTemplate rt = new RestTemplate();
        // 对 restTemplate 添加 日志拦截器
//        ClientHttpRequestInterceptor ri = new LoggingRequestInterceptor();
//        List<ClientHttpRequestInterceptor> ris = new ArrayList<ClientHttpRequestInterceptor>();
//        ris.add(ri);
//        rt.setInterceptors(ris);
//        rt.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        return rt;
    }
}
