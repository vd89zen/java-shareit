package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewCommentDto {
    @NotBlank(message = "Отсутствует текст комментария.")
    @Size(max = 1024, message = "Комментарий должен быть не более 1024 символов.")
    private String text;
}
