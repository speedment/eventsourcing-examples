package com.speedment.example.eventsourcing.booking.event.booking_event.generated;

import com.speedment.common.annotation.GeneratedCode;
import com.speedment.example.eventsourcing.booking.event.booking_event.BookingEvent;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.manager.AbstractManager;
import com.speedment.runtime.field.Field;
import java.util.stream.Stream;

/**
 * The generated base implementation for the manager of every {@link
 * com.speedment.example.eventsourcing.booking.event.booking_event.BookingEvent}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedBookingEventManagerImpl 
extends AbstractManager<BookingEvent> 
implements GeneratedBookingEventManager {
    
    private final TableIdentifier<BookingEvent> tableIdentifier;
    
    protected GeneratedBookingEventManagerImpl() {
        this.tableIdentifier = TableIdentifier.of("booking_demo", "booking_demo", "booking");
    }
    
    @Override
    public TableIdentifier<BookingEvent> getTableIdentifier() {
        return tableIdentifier;
    }
    
    @Override
    public Stream<Field<BookingEvent>> fields() {
        return Stream.of(
            BookingEvent.ID,
            BookingEvent.BOOKING,
            BookingEvent.TYPE,
            BookingEvent.VERSION,
            BookingEvent.USER_ID,
            BookingEvent.RESOURCE,
            BookingEvent.BOOK_FROM,
            BookingEvent.BOOK_TO
        );
    }
    
    @Override
    public Stream<Field<BookingEvent>> primaryKeyFields() {
        return Stream.of(
            BookingEvent.ID
        );
    }
}