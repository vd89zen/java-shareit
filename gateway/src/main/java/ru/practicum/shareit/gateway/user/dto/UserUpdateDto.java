package ru.practicum.shareit.gateway.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateDto {

    @Pattern(regexp = ".*\\S+.*",
            message = "(Если поле не null: должно содержать хотя бы один непробельный символ.")
    private String name;

    @Email(message = "Неверный формат адреса электронной почты.")
    private String email;
}
