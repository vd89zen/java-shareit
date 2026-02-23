package ru.practicum.shareit.server.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.request.dto.NewRequestItemDto;
import ru.practicum.shareit.server.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.server.request.model.RequestItem;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestItemMapper {

    public static RequestItemResponseDto toRequestItemResponseDto(RequestItem requestItem) {
        return RequestItemResponseDto.builder()
                .id(requestItem.getId())
                .description(requestItem.getDescription())
                .requestorId(requestItem.getRequestor().getId())
                .created(requestItem.getCreated())
                .build();
    }

    public static RequestItem toRequestItem(NewRequestItemDto newRequestItemDto) {
        return RequestItem.builder()
                .description(newRequestItemDto.getDescription())
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }
}
