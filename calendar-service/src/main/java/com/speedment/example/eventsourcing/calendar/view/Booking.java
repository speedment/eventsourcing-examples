package com.speedment.example.eventsourcing.calendar.view;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Data
public final class Booking {
    private final UUID id;
    private final UUID userId;
    private final UUID resourceId;
    private final LocalDateTime fromIncl;
    private final LocalDateTime toExcl;
}
