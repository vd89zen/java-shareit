package ru.practicum.shareit.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemShortDto {
    private Long id;
    private String name;
    private Long ownerId;
}
