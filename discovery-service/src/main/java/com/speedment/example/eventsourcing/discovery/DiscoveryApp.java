package com.speedment.example.eventsourcing.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@EnableEurekaServer
@SpringBootApplication
public class DiscoveryApp {

    public static void main(String... args) {
        SpringApplication.run(DiscoveryApp.class, args);
    }

}
