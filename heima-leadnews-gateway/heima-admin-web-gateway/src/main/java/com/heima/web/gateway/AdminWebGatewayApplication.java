package com.heima.web.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AdminWebGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminWebGatewayApplication.class,args);
    }
}
