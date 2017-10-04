package com.speedment.example.eventsourcing.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class CalendarApp {

    public static void main(String... args) {
        SpringApplication.run(CalendarApp.class, args);
    }

}