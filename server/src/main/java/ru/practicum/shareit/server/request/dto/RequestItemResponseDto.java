package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestItemResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Long requestorId;
    private List<ItemShortDto> items;
}
