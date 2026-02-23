package ru.practicum.shareit.gateway.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @NotBlank(message = "Отсутствует текст комментария.")
    @Size(max = 1024, message = "Комментарий должен быть не более 1024 символов.")
    private String text;
}
