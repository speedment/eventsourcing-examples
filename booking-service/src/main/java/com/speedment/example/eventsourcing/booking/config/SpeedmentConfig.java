package com.speedment.example.eventsourcing.booking.config;

import com.speedment.example.eventsourcing.booking.event.BookingsApplication;
import com.speedment.example.eventsourcing.booking.event.BookingsApplicationBuilder;
import com.speedment.example.eventsourcing.booking.event.booking_event.BookingEventManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Configuration
public class SpeedmentConfig {

    private @Autowired Environment env;

    @Bean
    BookingsApplication app() {
        return appBuilder().build();
    }

    @Bean
    BookingEventManager bookings(BookingsApplication app) {
        return app.getOrThrow(BookingEventManager.class);
    }

    private BookingsApplicationBuilder appBuilder() {
        return new BookingsApplicationBuilder()
            .withUsername(env.getProperty("speedment.dbms.username"))
            .withPassword(env.getProperty("speedment.dbms.password"));
    }
}
