package com.speedment.example.eventsourcing.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class ConfigApp {

    public static void main(String... args) {
        SpringApplication.run(ConfigApp.class, args);
    }

}
