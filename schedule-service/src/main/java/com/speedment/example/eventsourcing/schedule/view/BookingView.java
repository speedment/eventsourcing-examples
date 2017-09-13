package com.speedment.example.eventsourcing.schedule.view;

import com.speedment.example.eventsourcing.schedule.event.booking_event.BookingEvent;
import com.speedment.example.eventsourcing.schedule.event.booking_event.BookingEventImpl;
import com.speedment.example.eventsourcing.schedule.event.booking_event.BookingEventManager;
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

import static com.speedment.example.eventsourcing.schedule.view.BookingNotification.BookingStatus.ACCEPTED;
import static com.speedment.example.eventsourcing.schedule.view.BookingNotification.BookingStatus.REJECTED;
import static java.lang.String.format;
import static java.util.Arrays.asList;
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
    private final List<Consumer<BookingNotification>> listeners;
    private final Set<String> resources;
    private final AtomicLong lastEvent;

    BookingView(BookingEventManager manager) {
        this.manager   = requireNonNull(manager);
        this.bookings  = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
        this.lastEvent = new AtomicLong(-1);
        this.resources = new HashSet<>(asList("Room A", "Room B", "Room C"));
    }

    public Optional<Booking> findBooking(UUID uuid) {
        return Optional.ofNullable(bookings.get(uuid));
    }

    public Stream<Booking> currentBookings() {
        return bookings.values().stream();
    }

    public void addAcceptedListener(Consumer<BookingNotification> listener) {
        listeners.add(listener);
    }

    public void addRefusedListener(Consumer<BookingNotification> listener) {
        listeners.add(listener);
    }

    @PostConstruct
    void initialLoad() {
        pollForEvents();
    }

    @Scheduled(fixedRate = 1000)
    void pollForEvents() {
        final Queue<BookingNotification> notifications = new LinkedList<>();

        final AtomicBoolean foundEvents = new AtomicBoolean(true);
        while (foundEvents.compareAndSet(true, false)) {
            manager.stream()
                .filter(BookingEvent.ID.greaterThan(lastEvent.get()))
                .limit(1_000)
                .forEachOrdered(event -> {
                    foundEvents.set(true);
                    lastEvent.set(event.getId());
                    bookings.compute(event.getBooking(), (uuid, existing) -> {
                        if (existing == null) {
                            if (event.getType() != BookingEvent.Type.BOOK) {
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

                            if (!event.getResource().isPresent()) {
                                notifications.add(reject(event, "Missing resource name"));
                                return null;
                            }

                            if (!resources.contains(event.getResource().get())) {
                                notifications.add(reject(event, "Unrecognized resource name"));
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

                            final int userId         = event.getUserId().getAsInt();
                            final String resource    = event.getResource().get();
                            final LocalDateTime from = event.getBookFrom().get();
                            final LocalDateTime to   = event.getBookTo().get();

                            notifications.add(accept(new BookingEventImpl()
                                .setId(event.getId())
                                .setType(BookingEvent.Type.UPDATE)
                                .setVersion(event.getVersion())
                                .setBooking(event.getBooking())
                                .setUserId(userId)
                                .setResource(resource)
                                .setBookFrom(from)
                                .setBookTo(to)
                            ));

                            return new Booking(userId, resource, from, to);
                        } else {
                            if (event.getType() == BookingEvent.Type.UPDATE) {
                                final LocalDateTime from = event.getBookFrom().orElseGet(existing::getFromIncl);
                                final LocalDateTime to = event.getBookTo().orElseGet(existing::getToExcl);
                                final String resource = event.getResource().orElseGet(existing::getResource);
                                final int userId = event.getUserId().orElseGet(existing::getUserId);

                                if (!to.isAfter(from)) {
                                    notifications.add(reject(event, "bookTo must be after bookFrom"));
                                    return existing;
                                }

                                if (!resources.contains(resource)) {
                                    notifications.add(reject(event, "Unrecognized resource name"));
                                    return existing;
                                }

                                if (!isUnbooked(resource, from, to, b ->
                                        b.getUserId() != userId || !b.getResource().equals(resource))) {
                                    notifications.add(reject(event, "Resource already booked for specified time period"));
                                    return existing;
                                }

                                notifications.add(accept(new BookingEventImpl()
                                    .setId(event.getId())
                                    .setType(BookingEvent.Type.UPDATE)
                                    .setVersion(event.getVersion())
                                    .setBooking(event.getBooking())
                                    .setUserId(userId)
                                    .setResource(resource)
                                    .setBookFrom(from)
                                    .setBookTo(to)
                                ));

                                return new Booking(userId, resource, from, to);

                            } else if (event.getType() == BookingEvent.Type.CANCEL) {
                                notifications.add(accept(new BookingEventImpl()
                                    .setId(event.getId())
                                    .setType(BookingEvent.Type.CANCEL)
                                    .setVersion(event.getVersion())
                                    .setBooking(event.getBooking())
                                    .setUserId(existing.getUserId())
                                    .setResource(existing.getResource())
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
                    final BookingNotification notification = notifications.poll();
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

    private static BookingNotification accept(BookingEvent event) {
        return new BookingNotification(ACCEPTED, event, null);
    }

    private static BookingNotification reject(BookingEvent event, String msg) {
        return new BookingNotification(REJECTED, event, msg);
    }

    private boolean isUnbooked(BookingEvent event, Predicate<Booking> predicate) {
        return isUnbooked(
            event.getResource().get(),
            event.getBookFrom().get(),
            event.getBookTo().get(),
            predicate
        );
    }

    private boolean isUnbooked(
            String resource,
            LocalDateTime from,
            LocalDateTime to,
            Predicate<Booking> predicate) {

        return bookings.values().stream()
            .filter(b -> resource.equals(b.getResource()))
            .filter(predicate)
            .noneMatch(booking ->
                booking.getFromIncl().isBefore(to) &&
                booking.getToExcl().isAfter(from)
            );
    }
}
