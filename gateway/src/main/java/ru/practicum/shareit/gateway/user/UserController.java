package ru.practicum.shareit.gateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.user.dto.NewUserDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Creating user {}", newUserDto);
        return userClient.createUser(newUserDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Finding user {}", userId);
        return userClient.findUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser() {
        log.info("Finding all user");
        return userClient.findAllUser();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                                  @RequestBody @Valid UserUpdateDto userUpdateDto) {
        log.info("Updating user {}: {}", userId, userUpdateDto);
        return userClient.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("Deleting user {}", userId);
        return userClient.deleteUserById(userId);
    }
}
