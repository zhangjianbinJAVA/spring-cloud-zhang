package com.myke.admin.server;

import com.myke.admin.server.config.CustomBasicAuthHttpHeaderProvider;
import de.codecentric.boot.admin.config.EnableAdminServer;
import de.codecentric.boot.admin.web.client.HttpHeadersProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/22 11:29
 * ================================
 */
@EnableDiscoveryClient
@EnableAdminServer
@SpringBootApplication
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public HttpHeadersProvider httpHeadersProvider() {
        return new CustomBasicAuthHttpHeaderProvider();
    }
}
