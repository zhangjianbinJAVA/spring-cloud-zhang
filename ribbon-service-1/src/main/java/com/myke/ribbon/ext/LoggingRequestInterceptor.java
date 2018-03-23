package com.myke.ribbon.ext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * RestTemplate 拦截器
 *
 * user: zhangjianbin <br/>
 * date: 2018/1/18 15:29
 */
@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        log(request, body, response);

        return response;
    }

    private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        //do logging
        HttpHeaders headers = request.getHeaders();
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
        for (Map.Entry<String, List<String>> map:entries){
            String key = map.getKey();
            List<String> value = map.getValue();
            log.info("{},{}",key,value);
        }
        log.info("url:{},method:{},body:{},response status code:{}", request.getURI(), request.getMethod(), body, response.getStatusCode());
    }
}
