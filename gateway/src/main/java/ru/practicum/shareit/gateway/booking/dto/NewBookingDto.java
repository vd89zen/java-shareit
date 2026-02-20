package ru.practicum.shareit.gateway.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingDto {

	private long itemId;

	@NotNull(message = "Дата начала аренды не может быть null.")
	@FutureOrPresent
	private LocalDateTime start;

	@NotNull(message = "Дата окончания аренды не может быть null.")
	@Future
	private LocalDateTime end;
}
