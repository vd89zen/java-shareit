package ru.practicum.shareit.server.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody NewUserDto newUserDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userService.getUserResponseDto(
                                userService.createUser(newUserDto))
                );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable long userId) {
        return ResponseEntity
                .ok(
                        userService.getUserResponseDto(
                                userService.getUserById(userId))
                );
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUser() {
        return ResponseEntity
                .ok(
                        userService.getListUserResponseDto(
                                userService.getAllUser())
                );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable long userId,
                                                  @RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity
                .ok(
                        userService.getUserResponseDto(
                                userService.updateUserById(userId, userUpdateDto))
                );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity
                .noContent().build();
    }
}
