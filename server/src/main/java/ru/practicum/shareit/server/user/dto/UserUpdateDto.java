package ru.practicum.shareit.server.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class UserUpdateDto {
    private String name;
    private String email;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasEmail() {
        return email != null;
    }
}
