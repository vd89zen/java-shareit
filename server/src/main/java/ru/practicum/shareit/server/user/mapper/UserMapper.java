package ru.practicum.shareit.server.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(NewUserDto newUserDto) {
        return User.builder()
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }

    public static void updateUserFields(User updatingUser, UserUpdateDto userUpdateDto) {

        if (userUpdateDto.hasName()) {
            updatingUser.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.hasEmail()) {
            updatingUser.setEmail(userUpdateDto.getEmail());
        }

    }
}
