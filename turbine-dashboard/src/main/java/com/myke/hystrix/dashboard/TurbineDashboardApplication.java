package com.myke.hystrix.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * Turbine 在复杂的分布式系统中，相同服务的结点经常需要部署上百甚至上千个，
 * <p>
 * 很多时候，运维人员希望能够把相同服务的节点状态以一个整体集群的形式展现出来，
 * <p>
 * 这样可以更好的把握整个系统的状态。
 * <p>
 * 为此，Netflix提供了一个开源项目（Turbine） 来提供把多个hystrix.stream的内容聚合为
 * <p>
 * 一个数据源供Dashboard展示
 * <p>
 * 和Hystrix Dashboard一样，Turbine也可以下载war包部署到Web容器
 */
@EnableTurbine
@EnableHystrixDashboard
@EnableDiscoveryClient

@SpringBootApplication
public class TurbineDashboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(TurbineDashboardApplication.class);
    }


}
