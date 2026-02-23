package ru.practicum.shareit.gateway.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewRequestItemDto {
    @NotBlank(message = "Не указан текст запроса.")
    @Size(max = 1024, message = "Текст запроса должен быть не более 1024 символов.")
    private String description;
}
