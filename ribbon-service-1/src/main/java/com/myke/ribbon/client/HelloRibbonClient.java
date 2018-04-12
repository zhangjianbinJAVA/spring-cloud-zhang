package com.myke.ribbon.client;

import com.netflix.client.ClientFactory;
import com.netflix.client.IClient;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.niws.client.http.HttpClientRequest;
import com.netflix.niws.client.http.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.sound.midi.Soundbank;
import java.net.URI;
import java.net.URISyntaxException;

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

    public String retryHello() {
        RestClient client = null;
        ZoneAwareLoadBalancer lb = null;

        IClient namedClient = ClientFactory.getNamedClient("hello-service-1");

        if (namedClient instanceof RestClient) {
            client = (RestClient) namedClient;
            ILoadBalancer loadBalancer = client.getLoadBalancer();
            if (loadBalancer instanceof ZoneAwareLoadBalancer) {
                lb = (ZoneAwareLoadBalancer) loadBalancer;
                System.out.println(lb.getLoadBalancerStats());
            }
            try {
                HttpClientRequest request = HttpClientRequest.newBuilder().setUri(new URI("/")).build();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

        StringBuilder result = new StringBuilder();

        // GET
        String value = restTemplate.getForEntity(HELLO_SERVICE_URL + "/retry-hello", String.class).getBody();
        result.append(value);

        System.out.println(lb.getLoadBalancerStats());

        return result.toString();
    }


}
