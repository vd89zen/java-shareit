// Чтобы воспользоваться нужной вещью, её следует забронировать.
// Бронирование, или Booking, — ещё одна важная сущность приложения.
// Бронируется вещь всегда на определённые даты.
// Владелец вещи обязательно должен подтвердить бронирование.

package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker; // пользователь, который осуществляет бронирование;
    private Status status;

    public enum Status {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}