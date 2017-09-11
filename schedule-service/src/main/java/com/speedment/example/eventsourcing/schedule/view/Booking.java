package com.speedment.example.eventsourcing.schedule.view;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Data
public final class Booking {
    private final int userId;
    private final String resource;
    private final LocalDateTime fromIncl;
    private final LocalDateTime toExcl;
}
