package com.speedment.example.eventsourcing.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Configuration
public class RandomConfig {

    @Bean
    Random random() {
        final SecureRandom random = new SecureRandom();
        random.setSeed(System.currentTimeMillis());
        return random;
    }

}
