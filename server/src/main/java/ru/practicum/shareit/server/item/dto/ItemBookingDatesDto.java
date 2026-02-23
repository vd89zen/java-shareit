package ru.practicum.shareit.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class ItemBookingDatesDto {
    private Long itemId;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
}
