package ru.practicum.shareit.server.booking.dto;

import lombok.*;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemResponseDto item;
    private UserResponseDto booker;
    private String status;
}
