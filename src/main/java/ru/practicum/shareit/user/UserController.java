package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        UserMapper.toUserResponseDto(
                                userService.create(userDto))
                );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable @NotNull Long userId) {
        return ResponseEntity
                .ok(
                        UserMapper.toUserResponseDto(
                                userService.findById(userId))
                );
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity
                .ok(
                        userService.findAll().stream()
                                .map(UserMapper::toUserResponseDto)
                                .collect(Collectors.toUnmodifiableList())
                );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(@PathVariable @NotNull Long userId,
                                                  @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity
                .ok(
                        UserMapper.toUserResponseDto(
                                userService.update(userId, userUpdateDto))
                );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long userId) {
        userService.delete(userId);
        return ResponseEntity
                .noContent().build();
    }
}
