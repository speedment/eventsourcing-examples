package com.speedment.example.eventsourcing.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class EdgeApp {
//
//    @Bean
//    public RouteLocator customRouteLocator() {
//        return Routes.locator()
//            .route("test")
//            .uri("http://httpbin.org:80")
//            .predicate(host("**.abc.org").and(path("/image/png")))
//            .addResponseHeader("X-TestHeader", "foobar")
//            .and()
//            .route("test2")
//            .uri("http://httpbin.org:80")
//            .predicate(path("/image/webp"))
//            .add(addResponseHeader("X-AnotherHeader", "baz"))
//            .and()
//            .route("test3")
//            .order(-1)
//            .uri("http://httpbin.org:80")
//            .predicate(host("**.throttle.org").and(path("/get")))
//            .add(throttle.apply(tuple().of("capacity", 1,
//                "refillTokens", 1,
//                "refillPeriod", 10,
//                "refillUnit", "SECONDS")))
//            .and()
//            .build();
//    }

    public static void main(String... args) {
        SpringApplication.run(EdgeApp.class, args);
    }

}
