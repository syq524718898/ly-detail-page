package com.leyou.page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author sunyuqi
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableAutoConfiguration
public class LyPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyPageApplication.class);
    }
}
