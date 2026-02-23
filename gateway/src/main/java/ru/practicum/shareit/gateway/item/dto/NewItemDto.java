package ru.practicum.shareit.gateway.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewItemDto {

    @NotBlank(message = "Не указано название вещи.")
    @Size(max = 128, message = "Имя вещи должно быть не более 128 символов.")
    private String name;

    @NotBlank(message = "Не указано описание вещи.")
    @Size(max = 1024, message = "Описание вещи должно быть не более 1024 символов.")
    private String description;

    @NotNull(message = "Не указано доступна ли вещь для аренды.")
    private Boolean available;

    private Long requestId;
}
