package ru.practicum.shareit.gateway.item.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemUpdateDto {

    @Pattern(regexp = ".*\\S+.*",
            message = "(Если поле не null: должно содержать хотя бы один непробельный символ.")
    private String name;

    @Pattern(regexp = ".*\\S+.*",
            message = "(Если поле не null: должно содержать хотя бы один непробельный символ.")
    private String description;

    private Boolean available;
}
