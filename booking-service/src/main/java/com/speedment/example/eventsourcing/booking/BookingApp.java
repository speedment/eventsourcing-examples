package com.speedment.example.eventsourcing.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class BookingApp {

    public static void main(String... args) {
        SpringApplication.run(BookingApp.class, args);
    }

}