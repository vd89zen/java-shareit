package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UserInMemoryStorage implements UserStorage {
    private static Long nextId = 0L;
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(nextId, user);

        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId))
                .map(user -> User.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build());
    }

    @Override
    public List<User> findAll() {
        return users.values().stream()
                .map(user -> User.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public boolean delete(Long userId) {
        if (users.remove(userId) == null) {
            return false;
        }
        return true;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean isEmailAlreadyUse(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean isUserExists(Long userId) {
        return users.containsKey(userId);
    }

    private synchronized long getNextId() {
        return ++nextId;
    }
}
