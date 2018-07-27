//package com.myke.zuul.fallback;
//
//import com.google.gson.Gson;
//import com.netflix.hystrix.exception.HystrixTimeoutException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.stereotype.Component;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//
///**
// * Zuul 路由后面的微服务挂了后，Zuul 提供了一种回退机制来应对熔断处理
// */
//@Component
//public class RouteFallbackProvider implements FallbackProvider {
//
//    @Autowired
//    private Gson gson;
//
//    @Override
//    public String getRoute() {
//        // 表明是为哪个微服务提供回退，*表示为所有微服务提供回退
//        return "*";
//    }
//
//    @Override
//    public ClientHttpResponse fallbackResponse(Throwable cause) {
//        if (cause instanceof HystrixTimeoutException) {
//            return response(HttpStatus.GATEWAY_TIMEOUT);
//        } else {
//            return this.fallbackResponse();
//        }
//    }
//
//    @Override
//    public ClientHttpResponse fallbackResponse() {
//        return this.response(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    private ClientHttpResponse response(final HttpStatus status) {
//        return new ClientHttpResponse() {
//            @Override
//            public HttpStatus getStatusCode() throws IOException {
//                return status;
//            }
//
//            @Override
//            public int getRawStatusCode() throws IOException {
//                return status.value();
//            }
//
//            @Override
//            public String getStatusText() throws IOException {
//                return status.getReasonPhrase();
//            }
//
//            @Override
//            public void close() {
//            }
//
//            @Override
//            public InputStream getBody() throws IOException {
//
//                String route = "服务不可用，请稍后再试。";
//                return new ByteArrayInputStream(route.getBytes());
//            }
//
//            @Override
//            public HttpHeaders getHeaders() {
//                // headers设定
//                HttpHeaders headers = new HttpHeaders();
//                MediaType mt = new MediaType(MediaType.APPLICATION_JSON_UTF8, Charset.forName("UTF-8"));
//                headers.setContentType(mt);
//                return headers;
//            }
//        };
//    }
//}
