package com.myke.ribbon.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 15:10
 */
@Slf4j
@Service
public class HelloRibbonClient {

    private static final String HELLO_SERVICE_URL = "http://HELLO-SERVICE-1";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    public String index() {
        StringBuilder result = new StringBuilder();

        // GET
        String value = restTemplate.getForEntity(HELLO_SERVICE_URL + "/hello", String.class).getBody();
        result.append(value);

        return result.toString();
    }

}
