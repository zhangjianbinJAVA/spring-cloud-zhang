package com.myke.feign.config;

import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/6/26
 * Time： 11:27
 * ================================
 */

public class CustomLoadBalancerFeignClient extends LoadBalancerFeignClient {

    public CustomLoadBalancerFeignClient(Client delegate, CachingSpringLoadBalancerFactory lbClientFactory, SpringClientFactory clientFactory) {
        super(delegate, lbClientFactory, clientFactory);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        String url = request.url();
        URI uri = URI.create(url);
        String clientName = uri.getHost();
        String result = url.replaceFirst(clientName, clientName + "/user/moke/test/" + clientName.toLowerCase());

        Request request1 = Request.create(request.method(), result, request.headers(), request.body(), request.charset());

        return super.execute(request1, options);
    }

}
