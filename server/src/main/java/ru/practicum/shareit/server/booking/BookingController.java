package ru.practicum.shareit.server.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.dto.BookingState;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;

import java.util.List;
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                            @RequestBody NewBookingDto newBookingDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        bookingService.getBookingResponseDto(
                                bookingService.createBooking(userId, newBookingDto))
                );
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveOrRejectBookingById(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                                     @PathVariable long bookingId,
                                                                     @RequestParam(name = "approved") Boolean approved) {
        log.info("{} Подтверждение/отказ брони ID {} арендодателем {}", approved, bookingId, userId);
        return ResponseEntity
                .ok(
                        bookingService.getBookingResponseDto(
                                bookingService.approveOrRejectBookingById(userId, bookingId, approved))
                );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingByIdWithAccessCheck(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                                            @PathVariable long bookingId) {
        return ResponseEntity
                .ok(
                        bookingService.getBookingResponseDto(
                                bookingService.getBookingByIdWithAccessCheck(userId, bookingId))
                );
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsForBooker(
            @RequestHeader(value = "X-Sharer-User-Id") long userId, @RequestParam(name = "state") BookingState state,
            @RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size) {
        return ResponseEntity
                .ok(
                        bookingService.getListBookingResponseDto(
                                bookingService.getBookingsForBooker(userId, state, page, size))
                );
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getBookingsForOwner(
            @RequestHeader(value = "X-Sharer-User-Id") long userId, @RequestParam(name = "state") BookingState state,
            @RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size) {
        return ResponseEntity
                .ok(
                        bookingService.getListBookingResponseDto(
                                bookingService.getBookingsForOwner(userId, state, page, size))
                );
    }
}
