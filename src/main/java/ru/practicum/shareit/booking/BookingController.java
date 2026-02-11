package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.State;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                     @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        BookingMapper.toBookingResponseDto(
                                bookingService.create(userId, bookingDto))
                );
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveOrRejectBooking(
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
            @PathVariable(required = true) Long bookingId, @RequestParam(required = true) Boolean approved) {
        return ResponseEntity
                .ok(
                        BookingMapper.toBookingResponseDto(
                                bookingService.approveOrRejectBooking(userId, bookingId, approved))
                );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingWithAccessCheck(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                                        @PathVariable(required = true) Long bookingId) {
        return ResponseEntity
                .ok(
                        BookingMapper.toBookingResponseDto(
                                bookingService.getBookingWithAccessCheck(userId, bookingId))
                );
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsForBooker(
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
            @RequestParam(defaultValue = "ALL") State state) {
        return ResponseEntity
                .ok(
                        bookingService.getBookingsForBooker(userId, state).stream()
                                .map(BookingMapper::toBookingResponseDto)
                                .collect(Collectors.toUnmodifiableList())
                );
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getBookingsForOwner(
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
            @RequestParam(defaultValue = "ALL") State state) {
        return ResponseEntity
                .ok(
                        bookingService.getBookingsForOwner(userId, state).stream()
                                .map(BookingMapper::toBookingResponseDto)
                                .collect(Collectors.toUnmodifiableList())
                );
    }
}
