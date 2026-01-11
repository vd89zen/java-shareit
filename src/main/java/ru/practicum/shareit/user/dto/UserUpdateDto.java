package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDto {
    @Pattern(regexp = ".*\\S+.*",
            message = "(Если поле не null: должно содержать хотя бы один непробельный символ.")
    private String name;
    @Email(message = "Неверный формат адреса электронной почты.")
    private String email;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasEmail() {
        return email != null;
    }
}
