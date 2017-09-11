package com.speedment.example.eventsourcing.schedule.view;

import com.speedment.example.eventsourcing.schedule.event.booking_event.BookingEvent;
import lombok.Data;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Data
public final class BookingNotification {

    enum BookingStatus {
        ACCEPTED, REJECTED
    }

    private final BookingStatus status;
    private final BookingEvent data;
    private final String msg;

}