package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    void update(User user);

    boolean delete(Long userId);

    Optional<User> findById(Long userId);

    List<User> findAll();

    Optional<User> findByEmail(String email);

    boolean isEmailAlreadyUse(String email);

    boolean isUserExists(Long userId);
}
