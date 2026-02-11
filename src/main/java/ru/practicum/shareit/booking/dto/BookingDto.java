package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    @NotNull(message = "Дата начала аренды не может быть null.")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания аренды не может быть null.")
    private LocalDateTime end;

    @NotNull(message = "Не указан ID вещи.")
    private Long itemId;
}
