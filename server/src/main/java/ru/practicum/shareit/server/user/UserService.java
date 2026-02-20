package ru.practicum.shareit.server.user;

import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(NewUserDto newUserDto);

    User getUserById(Long userId);

    List<User> getAllUser();

    User updateUserById(Long userId, UserUpdateDto userUpdateDto);

    void deleteUserById(Long userId);

    void checkUserExists(Long userId);

    void checkEmailUse(String email);

    UserResponseDto getUserResponseDto(User user);

    List<UserResponseDto> getListUserResponseDto(List<User> users);

}
