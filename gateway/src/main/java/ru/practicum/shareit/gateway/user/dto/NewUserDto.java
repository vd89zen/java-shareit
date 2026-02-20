package ru.practicum.shareit.gateway.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @NotBlank(message = "Не указано имя пользователя.")
    private String name;

    @NotBlank(message = "Не указана электронная почта.")
    @Email(message = "Неверный формат адреса электронной почты.")
    private String email;
}
