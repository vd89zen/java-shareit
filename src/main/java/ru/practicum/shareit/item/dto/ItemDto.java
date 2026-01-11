package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    @NotBlank(message = "Не указано название вещи.")
    private String name;
    @NotBlank(message = "Не указано описание вещи.")
    private String description;
    @NotNull(message = "Не указано доступна ли вещь для аренды.")
    private Boolean available;
    private Long requestId;
}
