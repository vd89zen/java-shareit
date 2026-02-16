package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User create(UserDto userDto);

    User findById(Long userId);

    List<User> findAll();

    User update(Long userId, UserUpdateDto userUpdateDto);

    void delete(Long userId);

    void checkUserExists(Long userId);

    void checkEmailUse(String email);

    UserResponseDto getUserResponseDto(User user);

    List<UserResponseDto> getListUserResponseDto(List<User> users);

}
