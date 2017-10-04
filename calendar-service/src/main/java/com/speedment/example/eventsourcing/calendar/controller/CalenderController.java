package com.speedment.example.eventsourcing.calendar.controller;

import com.speedment.example.eventsourcing.calendar.view.Booking;
import com.speedment.example.eventsourcing.calendar.view.BookingView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.notFound;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@RestController
public final class CalenderController {

    private @Autowired BookingView view;

    @GetMapping
    List<Booking> allBookings() {
        return view.currentBookings().collect(toList());
    }

    @GetMapping("/{uuid}")
    ResponseEntity<Booking> bookingById(@PathVariable UUID uuid) {
        return view.findBooking(uuid)
            .map(ResponseEntity::ok)
            .orElseGet(() -> notFound().build());
    }

    @GetMapping("/byResource/{resource}")
    List<Booking> bookingsByResource(@PathVariable UUID resource) {
        return view.currentBookings()
            .filter(b -> b.getResourceId().equals(resource))
            .collect(toList());
    }

    @GetMapping("/byUser/{user}")
    List<Booking> bookingsByUser(@PathVariable UUID user) {
        return view.currentBookings()
            .filter(b -> b.getUserId() == user)
            .collect(toList());
    }
}
