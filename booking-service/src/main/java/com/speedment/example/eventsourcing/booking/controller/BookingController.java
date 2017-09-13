package com.speedment.example.eventsourcing.booking.controller;

import com.speedment.example.eventsourcing.booking.event.booking_event.BookingEvent;
import com.speedment.example.eventsourcing.booking.event.booking_event.BookingEventImpl;
import com.speedment.example.eventsourcing.booking.event.booking_event.BookingEventManager;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
public final class BookingController {

    private @Autowired BookingEventManager bookingEvents;
    private @Value("${booking.apiVersion:1}") byte apiVersion;

    @PostMapping
    ResponseEntity<EventId> createBooking(@RequestBody Booking booking) {
        final UUID uuid = UUID.randomUUID();
        final BookingEvent event = bookingEvents.persist(newEvent(booking)
            .setBooking(uuid)
            .setType(BookingEvent.Type.BOOK)
        );

        return trackProgress(event.getBooking());
    }

    @PutMapping("{uuid}")
    ResponseEntity<EventId> updateBooking(
            @PathVariable("uuid") UUID id,
            @RequestBody Booking booking) {

        final BookingEvent event = bookingEvents.persist(newEvent(booking)
            .setBooking(id)
            .setType(BookingEvent.Type.UPDATE)
        );

        return trackProgress(event.getBooking());
    }

    @DeleteMapping("{uuid}")
    ResponseEntity<EventId> cancelBooking(@PathVariable("uuid") UUID id) {
        final BookingEvent event = bookingEvents.persist(new BookingEventImpl()
            .setBooking(id)
            .setType(BookingEvent.Type.CANCEL)
        );

        return trackProgress(event.getBooking());
    }

    private BookingEvent newEvent(Booking booking) {
        return new BookingEventImpl()
            .setVersion(apiVersion)
            .setUserId(booking.getUserId())
            .setResource(booking.getResource())
            .setBookFrom(booking.getBookFrom().toLocalDateTime())
            .setBookTo(booking.getBookTo().toLocalDateTime());
    }

    private ResponseEntity<EventId> trackProgress(UUID uuid) {
        return created(fromPath("/booking/{uuid}")
            .build(uuid)
        ).body(new EventId(uuid));
    }

    @Data
    private final static class EventId {
        private final UUID eventId;
    }

    @Data
    private final static class Booking {
        private @NotNull int userId;
        private @NotNull String resource;
        private @NotNull OffsetDateTime bookFrom;
        private @NotNull OffsetDateTime bookTo;
    }
}
