package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemShortDto {
    private Long id;
    private String name;
}
