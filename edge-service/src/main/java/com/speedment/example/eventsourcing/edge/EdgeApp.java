package com.speedment.example.eventsourcing.edge;

import com.github.mthizo247.cloud.netflix.zuul.web.socket.EnableZuulWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableZuulWebSocket
@EnableWebSocketMessageBroker
@EnableWebSecurity
public class EdgeApp {

    public static void main(String... args) {
        SpringApplication.run(EdgeApp.class, args);
    }
}