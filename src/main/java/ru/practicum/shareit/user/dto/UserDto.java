package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @NotBlank(message = "Не указано имя пользователя.")
    private String name;
    @NotBlank(message = "Не указана электронная почта.")
    @Email(message = "Неверный формат адреса электронной почты.")
    private String email;
}
