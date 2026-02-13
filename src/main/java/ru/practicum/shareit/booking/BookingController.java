package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                     @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        bookingService.getBookingResponseDto(
                                bookingService.create(userId, bookingDto))
                );
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveOrRejectBooking(
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
            @PathVariable(required = true) Long bookingId, @RequestParam(required = true) Boolean approved) {
        return ResponseEntity
                .ok(
                        bookingService.getBookingResponseDto(
                                bookingService.approveOrRejectBooking(userId, bookingId, approved))
                );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingWithAccessCheck(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
                                                                        @PathVariable(required = true) Long bookingId) {
        return ResponseEntity
                .ok(
                        bookingService.getBookingResponseDto(
                                bookingService.getBookingWithAccessCheck(userId, bookingId))
                );
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsForBooker(
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
            @RequestParam(defaultValue = "ALL") State state) {
        return ResponseEntity
                .ok(
                        bookingService.getListBookingResponseDto(
                                bookingService.getBookingsForBooker(userId, state))
                );
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getBookingsForOwner(
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId,
            @RequestParam(defaultValue = "ALL") State state) {
        return ResponseEntity
                .ok(
                        bookingService.getListBookingResponseDto(
                                bookingService.getBookingsForOwner(userId, state))
                );
    }
}
