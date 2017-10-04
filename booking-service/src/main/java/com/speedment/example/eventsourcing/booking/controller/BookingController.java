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
    ResponseEntity<BookingReceipt> createBooking(
            @RequestBody BookingRequest bookingRequest) {

        return createReceipt(
            bookingEvents.persist(
                newBookingEvent(bookingRequest)
                    .setBookingId(UUID.randomUUID())
                    .setType(BookingEvent.Type.CREATE_BOOKING)
            )
        );
    }

    @PutMapping("{uuid}")
    ResponseEntity<BookingReceipt> updateBooking(
            @PathVariable("uuid") UUID bookingId,
            @RequestBody BookingRequest booking) {

        return createReceipt(
            bookingEvents.persist(
                newBookingEvent(booking)
                    .setBookingId(bookingId)
                    .setType(BookingEvent.Type.UPDATE_BOOKING)
            )
        );
    }

    @DeleteMapping("{uuid}")
    ResponseEntity<BookingReceipt> cancelBooking(
            @PathVariable("uuid") UUID bookingId) {

        return createReceipt(
            bookingEvents.persist(
                newBookingEvent()
                    .setBookingId(bookingId)
                    .setType(BookingEvent.Type.CANCEL_BOOKING)
                )
        );
    }

    private BookingEvent newBookingEvent() {
        return new BookingEventImpl()
            .setVersion(apiVersion);
    }

    private BookingEvent newBookingEvent(BookingRequest booking) {
        return newBookingEvent()
            .setUserId(booking.getUser())
            .setResourceId(booking.getResource())
            .setBookFrom(booking.getBookFrom().toLocalDateTime())
            .setBookTo(booking.getBookTo().toLocalDateTime());
    }

    private ResponseEntity<BookingReceipt> createReceipt(BookingEvent event) {
        return created(fromPath("/booking/{uuid}")
            .build(event.getBookingId())
        ).body(new BookingReceipt(
            event.getSeqNo(),
            event.getBookingId()
        ));
    }

    @Data
    private final static class BookingRequest {
        private @NotNull UUID user;
        private @NotNull UUID resource;
        private @NotNull OffsetDateTime bookFrom;
        private @NotNull OffsetDateTime bookTo;
    }

    @Data
    private final static class BookingReceipt {
        private final long seqNo;
        private final UUID eventId;
    }
}
