package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationError;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND = "Пользователь с id = %d не найден.";
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(NewUserDto newUserDto) {
        log.info("Создание нового пользователя: {}.", newUserDto);

        checkEmailUse(newUserDto.getEmail());

        User newUser = UserMapper.toUser(newUserDto);
        newUser = userRepository.save(newUser);
        log.info("Создан новый пользователь: {}.", newUser);
        return newUser;
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Поиск пользователя ID {}.", userId);
        return getUserOrThrow(userId);
    }

    @Override
    public List<User> getAllUser() {
        log.info("Получение списка всех пользователей.");
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUserById(Long userId, UserUpdateDto userUpdateDto) {
        log.info("Обновление пользователя ID {}: {}.", userId, userUpdateDto);

        if (userUpdateDto.hasEmail()) {
            checkEmailUse(userUpdateDto.getEmail());
        }

        User updatingUser = getUserOrThrow(userId);
        UserMapper.updateUserFields(updatingUser, userUpdateDto);
        updatingUser = userRepository.save(updatingUser);

        log.info("Обновлен пользователь {}.", updatingUser);
        return updatingUser;
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Удаление пользователя ID {}.", userId);
        userRepository.deleteById(userId);
        log.info("Удален пользователь ID {}.", userId);
    }

    @Override
    public void checkUserExists(Long userId) {
        log.info("Проверяем существование пользователя ID {}.", userId);
        if (userRepository.existsById(userId) == false) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }
        log.info("Пользователь ID {} существует.", userId);
    }

    @Override
    public void checkEmailUse(String email) {
        log.info("Проверяем занят ли email {}.", email);
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException(ValidationError.builder()
                    .field("email")
                    .message("Данный email уже используется.")
                    .rejectedValue(email)
                    .build());
        }
        log.info("email {} свободен.", email);
    }

    @Override
    public UserResponseDto getUserResponseDto(User user) {
        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getListUserResponseDto(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        log.info("Получаем пользователя ID {} (либо ошибку).", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, userId)));
    }
}
