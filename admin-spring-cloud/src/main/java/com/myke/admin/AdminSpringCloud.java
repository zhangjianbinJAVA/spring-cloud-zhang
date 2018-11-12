package com.myke.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/8/21
 * Time： 11:24
 * ================================
 */
@EnableAdminServer
@SpringBootApplication
public class AdminSpringCloud {
    public static void main(String[] args) {
        SpringApplication.run(AdminSpringCloud.class, args);
    }
}
