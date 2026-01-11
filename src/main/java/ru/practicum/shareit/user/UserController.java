package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForResponse;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

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
    public ResponseEntity<UserDtoForResponse> create(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(userDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDtoForResponse> findById(@PathVariable @NotNull Long userId) {
        return ResponseEntity
                .ok(userService.findById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDtoForResponse>> findAll() {
        return ResponseEntity
                .ok(userService.findAll());
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDtoForResponse> update(@PathVariable @NotNull Long userId,
                                                     @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity
                .ok(userService.update(userId, userUpdateDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
