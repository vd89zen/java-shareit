package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@EqualsAndHashCode(of = {"id", "email"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoForResponse {
    private Long id;
    private String name;
    private String email;
}
