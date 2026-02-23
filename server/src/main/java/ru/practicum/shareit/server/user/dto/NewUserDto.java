package ru.practicum.shareit.server.user.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    private String name;
    private String email;
}
