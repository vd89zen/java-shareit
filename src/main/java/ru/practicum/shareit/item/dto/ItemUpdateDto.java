package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemUpdateDto {
    @Pattern(regexp = ".*\\S+.*",
            message = "(Если поле не null: должно содержать хотя бы один непробельный символ.")
    private String name;
    @Pattern(regexp = ".*\\S+.*",
            message = "(Если поле не null: должно содержать хотя бы один непробельный символ.")
    private String description;
    private Boolean available;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasAvailable() {
        return available != null;
    }

}
