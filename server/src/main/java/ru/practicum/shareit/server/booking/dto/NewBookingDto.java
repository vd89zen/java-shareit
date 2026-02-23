package ru.practicum.shareit.server.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewBookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
