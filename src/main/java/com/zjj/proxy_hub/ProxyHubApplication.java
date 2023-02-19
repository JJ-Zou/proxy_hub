package com.zjj.proxy_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProxyHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyHubApplication.class, args);
    }

}
