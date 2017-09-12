package com.speedment.example.eventsourcing.edge;

import com.github.mthizo247.cloud.netflix.zuul.web.socket.EnableZuulWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
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
public class EdgeApp {
//
//    @Bean
//    public RouteLocator customRouteLocator() {
//        return Routes.locator()
//            .route("booking-route")
//            .id("booking-service")
//            .uri("http://localhost:9090/booking")
//            .predicate(path("/api/v1/booking")
//                .and(method("POST").or(method("PUT")).or(method("DELETE"))))
//            .add(addResponseHeader("", ""))
//            .addResponseHeader("X-Service", "booking-service")
//            .add((exchange, chain) -> {
//                chain.filter(exchange);
//                return exchange.getResponse().setComplete();
//            })
//            .and()
//            .route("schedule-route")
//            .id("schedule-service")
//            .uri("http://localhost:9091/booking")
//            .predicate(path("/api/v1/booking").and(method("GET")))
//            .addResponseHeader("X-Service", "schedule-service")
//            .and()
//            .route("websocket-route")
//            .id("websocket-service")
//            .uri("ws://localhost:9091/subscribe")
//            .predicate(path("/api/v1/subscribe"))
//            .addResponseHeader("X-Service", "websocket-service")
//            .and()
//            .route("frontend-route")
//            .id("frontend-service")
//            .uri("http://localhost:9092")
//            .predicate(method("GET"))
//            .addResponseHeader("X-Service", "frontend-service")
//            .and().build();
//    }

    public static void main(String... args) {
        SpringApplication.run(EdgeApp.class, args);
    }
}