package ru.practicum.shareit.server.item.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
