package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    Booking create(Long bookerId, BookingDto bookingDto);

    Booking approveOrRejectBooking(Long userId, Long bookingId, Boolean approved);

    Booking getBookingWithAccessCheck(Long userId, Long bookingId);

    List<Booking> getBookingsForBooker(Long bookerId, State state);

    List<Booking> getBookingsForOwner(Long ownerId, State state);

    void checkBookingExists(Long bookingId);

}
