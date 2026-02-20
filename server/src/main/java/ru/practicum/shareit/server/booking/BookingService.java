package ru.practicum.shareit.server.booking;

import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.dto.BookingState;

import java.util.List;

public interface BookingService {

    Booking createBooking(Long bookerId, NewBookingDto newBookingDto);

    Booking approveOrRejectBookingById(Long userId, Long bookingId, Boolean approved);

    Booking getBookingByIdWithAccessCheck(Long userId, Long bookingId);

    List<Booking> getBookingsForBooker(Long bookerId, BookingState state, Integer page, Integer size);

    List<Booking> getBookingsForOwner(Long ownerId, BookingState state, Integer page, Integer size);

    void checkBookingExists(Long bookingId);

    BookingResponseDto getBookingResponseDto(Booking booking);

    List<BookingResponseDto> getListBookingResponseDto(List<Booking> bookings);

}
