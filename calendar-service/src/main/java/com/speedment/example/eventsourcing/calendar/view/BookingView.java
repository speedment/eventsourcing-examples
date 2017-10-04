package com.speedment.example.eventsourcing.calendar.view;

import com.speedment.example.eventsourcing.calendar.event.booking_event.BookingEvent;
import com.speedment.example.eventsourcing.calendar.event.booking_event.BookingEventImpl;
import com.speedment.example.eventsourcing.calendar.event.booking_event.BookingEventManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.speedment.example.eventsourcing.calendar.view.BookingConfirmation.BookingStatus.ACCEPTED;
import static com.speedment.example.eventsourcing.calendar.view.BookingConfirmation.BookingStatus.REJECTED;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Service
@EnableScheduling
public final class BookingView {

    private final BookingEventManager manager;
    private final Map<UUID, Booking> bookings;
    private final List<Consumer<BookingConfirmation>> listeners;
    private final AtomicLong lastEvent;

    BookingView(BookingEventManager manager) {
        this.manager   = requireNonNull(manager);
        this.bookings  = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
        this.lastEvent = new AtomicLong(-1);
    }

    public Optional<Booking> findBooking(UUID uuid) {
        return Optional.ofNullable(bookings.get(uuid));
    }

    public Stream<Booking> currentBookings() {
        return bookings.values().stream();
    }

    public void addAcceptedListener(Consumer<BookingConfirmation> listener) {
        listeners.add(listener);
    }

    public void addRefusedListener(Consumer<BookingConfirmation> listener) {
        listeners.add(listener);
    }

    @PostConstruct
    void initialLoad() {
        pollForEvents();
    }

    @Scheduled(fixedRate = 1000)
    void pollForEvents() {
        final Queue<BookingConfirmation> notifications = new LinkedList<>();

        final AtomicBoolean foundEvents = new AtomicBoolean(true);
        while (foundEvents.compareAndSet(true, false)) {
            manager.stream()
                .filter(BookingEvent.SEQ_NO.greaterThan(lastEvent.get()))
                .limit(1_000)
                .forEachOrdered(event -> {
                    foundEvents.set(true);
                    lastEvent.set(event.getSeqNo());
                    bookings.compute(event.getBookingId(), (uuid, existing) -> {
                        if (existing == null) {
                            if (event.getType() != BookingEvent.Type.CREATE_BOOKING) {
                                notifications.add(reject(event, "Unrecognized booking id"));
                                return null;
                            }

                            if (!event.getUserId().isPresent()) {
                                notifications.add(reject(event, "Missing userId"));
                                return null;
                            }

                            if (!event.getBookFrom().isPresent()) {
                                notifications.add(reject(event, "Missing bookFrom"));
                                return null;
                            }

                            if (!event.getBookTo().isPresent()) {
                                notifications.add(reject(event, "Missing bookTo"));
                                return null;
                            }

                            if (!event.getResourceId().isPresent()) {
                                notifications.add(reject(event, "Missing resource name"));
                                return null;
                            }

                            if (!event.getBookTo().filter(date -> date.isAfter(event.getBookFrom().get())).isPresent()) {
                                notifications.add(reject(event, "bookTo mus tbe after bookFrom"));
                                return null;
                            }

                            if (!isUnbooked(event, e -> true)) {
                                notifications.add(reject(event, "Resource already booked for specified time period"));
                                return null;
                            }

                            final UUID id            = event.getBookingId();
                            final UUID user          = event.getUserId().get();
                            final UUID resource      = event.getResourceId().get();
                            final LocalDateTime from = event.getBookFrom().get();
                            final LocalDateTime to   = event.getBookTo().get();

                            notifications.add(accept(new BookingEventImpl()
                                .setSeqNo(event.getSeqNo())
                                .setVersion(event.getVersion())
                                .setType(BookingEvent.Type.UPDATE_BOOKING)
                                .setBookingId(id)
                                .setUserId(user)
                                .setResourceId(resource)
                                .setBookFrom(from)
                                .setBookTo(to)
                            ));

                            return new Booking(id, user, resource, from, to);
                        } else {
                            if (event.getType() == BookingEvent.Type.UPDATE_BOOKING) {
                                final LocalDateTime from = event.getBookFrom().orElseGet(existing::getFromIncl);
                                final LocalDateTime to = event.getBookTo().orElseGet(existing::getToExcl);
                                final UUID resourceId = event.getResourceId().orElseGet(existing::getResourceId);
                                final UUID userId = event.getUserId().orElseGet(existing::getUserId);

                                if (!to.isAfter(from)) {
                                    notifications.add(reject(event, "bookTo must be after bookFrom"));
                                    return existing;
                                }

                                if (!isUnbooked(resourceId, from, to, b ->
                                        b.getUserId() != userId || !b.getResourceId().equals(resourceId))) {
                                    notifications.add(reject(event, "Resource already booked for specified time period"));
                                    return existing;
                                }

                                notifications.add(accept(new BookingEventImpl()
                                    .setSeqNo(event.getSeqNo())
                                    .setType(BookingEvent.Type.UPDATE_BOOKING)
                                    .setVersion(event.getVersion())
                                    .setBookingId(event.getBookingId())
                                    .setUserId(userId)
                                    .setResourceId(resourceId)
                                    .setBookFrom(from)
                                    .setBookTo(to)
                                ));

                                return new Booking(
                                    event.getBookingId(),
                                    userId, resourceId,
                                    from, to
                                );

                            } else if (event.getType() == BookingEvent.Type.CANCEL_BOOKING) {
                                notifications.add(accept(new BookingEventImpl()
                                    .setSeqNo(event.getSeqNo())
                                    .setType(BookingEvent.Type.CANCEL_BOOKING)
                                    .setVersion(event.getVersion())
                                    .setBookingId(event.getBookingId())
                                    .setUserId(existing.getUserId())
                                    .setResourceId(existing.getResourceId())
                                    .setBookFrom(existing.getFromIncl())
                                    .setBookTo(existing.getToExcl())
                                ));
                                return null; // Remove the booking.
                            } else {
                                notifications.add(reject(event,
                                    "Event type not allowed for existing bookings"
                                ));
                                return existing;
                            }
                        }
                    });
                });

            if (!notifications.isEmpty()) {
                final AtomicInteger accepted = new AtomicInteger(0);
                final Map<String, AtomicInteger> rejected = new HashMap<>();

                while (!notifications.isEmpty()) {
                    final BookingConfirmation notification = notifications.poll();
                    listeners.forEach(listener -> {
                        listener.accept(notification);
                        if (notification.getStatus() == ACCEPTED) {
                            accepted.getAndIncrement();
                        } else {
                            rejected.computeIfAbsent(
                                notification.getMsg(),
                                s -> new AtomicInteger()
                            ).getAndIncrement();
                        }
                    });
                }

                final int rejectedCount = rejected.values().stream()
                    .mapToInt(AtomicInteger::get).sum();

                System.out.format(
                    "Finished loading %d events. %d accepted and %d rejected.%n%s",
                    accepted.get() + rejectedCount,
                    accepted.get(),
                    rejectedCount,
                    rejected.entrySet().stream()
                        .map(e -> format("...%s: %d%n", e.getKey(), e.getValue().get()))
                        .collect(joining())
                );
            }
        }
    }

    private static BookingConfirmation accept(BookingEvent event) {
        return new BookingConfirmation(ACCEPTED, event, null);
    }

    private static BookingConfirmation reject(BookingEvent event, String msg) {
        return new BookingConfirmation(REJECTED, event, msg);
    }

    private boolean isUnbooked(BookingEvent event, Predicate<Booking> predicate) {
        return isUnbooked(
            event.getResourceId().get(),
            event.getBookFrom().get(),
            event.getBookTo().get(),
            predicate
        );
    }

    private boolean isUnbooked(
            UUID resource,
            LocalDateTime from,
            LocalDateTime to,
            Predicate<Booking> predicate) {

        return bookings.values().stream()
            .filter(b -> resource.equals(b.getResourceId()))
            .filter(predicate)
            .noneMatch(booking ->
                booking.getFromIncl().isBefore(to) &&
                booking.getToExcl().isAfter(from)
            );
    }
}
