package ru.practicum.shareit.gateway.booking;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
										   @RequestBody @Valid NewBookingDto newBookingDto) {
		log.info("Creating booking {}, userId={}", newBookingDto, userId);
		return bookingClient.createBooking(userId, newBookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") long userId,
														 @PathVariable long bookingId,
														 @RequestParam(name = "approved") @NotNull Boolean approved) {
		log.info("Approve ({}) booking {}, userId={}", approved, bookingId, userId);
		return bookingClient.approveOrRejectBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsForBooker(@RequestHeader("X-Sharer-User-Id") long userId,
													   @RequestParam(name = "state", defaultValue = "all") String stateParam,
													   @PositiveOrZero @RequestParam(name = "page", defaultValue = "0") Integer page,
													   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get booking with state {}, userId={}, page={}, size={}", stateParam, userId, page, size);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getBookingsForBooker(userId, state, page, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
													  @RequestParam(name = "state", defaultValue = "all") String stateParam,
													  @PositiveOrZero @RequestParam(name = "page", defaultValue = "0") Integer page,
													  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("For owner: Get booking with state {}, userId={}, page={}, size={}", stateParam, userId, page, size);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getBookingsForOwner(userId, state, page, size);
	}
}
