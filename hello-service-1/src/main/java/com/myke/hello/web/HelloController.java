package com.myke.hello.web;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/18 13:50
 */
@Slf4j
@RestController
public class HelloController {
    private final static Logger LOG = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private DiscoveryClient client;

    @GetMapping("/hello")
    public String index() {
        ServiceInstance instance = client.getLocalServiceInstance();
        log.info("/hello host:{},serviceId:{}", instance.getHost(), instance.getServiceId());
        return "Hello World";
    }

    @GetMapping("/headers")
    public Map<String, String> gethead(HttpServletRequest request) {
        String header = request.getHeader("X-ECC-Zuul-Forwarded-Domain");
        LOG.info("X-ECC-Zuul-Forwarded-Domain:{}",header);

        Map<String, String> headersInfo = getHeadersInfo(request);
        LOG.info("headersInfo:{}", headersInfo);
        return headersInfo;
    }


    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        String forwardedHost = request.getHeader("x-forwarded-host");
        LOG.info("x-forwarded-host:{}", forwardedHost);


        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
