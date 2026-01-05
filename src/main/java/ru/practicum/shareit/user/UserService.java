package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationError;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final String USER_NOT_FOUND = "Пользователь с id = %d не найден.";
    private final UserStorage userStorage;

    public UserDtoForResponse create(UserDto userDto) {
        log.info("Создание нового пользователя: {}.", userDto);

        checkEmailUse(userDto.getEmail());

        User newUser = UserMapper.toUser(userDto);
        newUser = userStorage.create(newUser);
        log.info("Создан новый пользователь: {}.", newUser);
        return UserMapper.toUserDtoForResponse(newUser);
    }

    public UserDtoForResponse findById(Long userId) {
        log.info("Поиск пользователя ID {}.", userId);
        User user = getUserOrThrow(userId);
        return UserMapper.toUserDtoForResponse(user);
    }

    public List<UserDtoForResponse> findAll() {
        log.info("Получение списка всех пользователей.");
        List<User> users = userStorage.findAll();

        return users.stream()
                .map(UserMapper::toUserDtoForResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    public UserDtoForResponse update(Long userId, UserUpdateDto userUpdateDto) {
        log.info("Обновление пользователя ID {}: {}.", userId, userUpdateDto);

        if (userUpdateDto.hasEmail()) {
            checkEmailUse(userUpdateDto.getEmail());
        }

        User updatingUser = getUserOrThrow(userId);
        UserMapper.updateUserFields(updatingUser, userUpdateDto);
        userStorage.update(updatingUser);

        return UserMapper.toUserDtoForResponse(updatingUser);
    }

    public void delete(Long userId) {
        log.info("Удаление пользователя ID {}.", userId);
        if (userStorage.delete(userId) == false) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }

    public void checkUserExists(Long userId) {
        if (userStorage.isUserExists(userId) == false) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }

    private void checkEmailUse(String email) {
        if (userStorage.isEmailAlreadyUse(email)) {
            throw new ValidationException(ValidationError.builder()
                    .field("email")
                    .message("Данный email уже используется.")
                    .rejectedValue(email)
                    .build());
        }
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, userId)));
    }
}
