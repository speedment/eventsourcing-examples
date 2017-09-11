package com.speedment.example.eventsourcing.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigApp {
    public static void main(String... args) {
        SpringApplication.run(ConfigApp.class, args);
    }
}