package ru.practicum.shareit.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Тесты для UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание пользователя — успешный ответ (CREATED)")
    void createUser_Should_Return_Created_Test() throws Exception {
        // Given
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Test User");
        newUserDto.setEmail("test@example.com");

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("Test User");
        userResponseDto.setEmail("test@example.com");

        when(userService.createUser(any(NewUserDto.class))).thenReturn(new User());
        when(userService.getUserResponseDto(any(User.class))).thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).createUser(any(NewUserDto.class));
    }


    @Test
    @DisplayName("Получение пользователя по ID — успешный ответ")
    void getUserById_Should_Return_User_Test() throws Exception {
        // Given
        long userId = 1L;

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userId);
        userResponseDto.setName("Existing User");
        userResponseDto.setEmail("existing@example.com");

        when(userService.getUserById(eq(userId))).thenReturn(new User());
        when(userService.getUserResponseDto(any(User.class))).thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Existing User"))
                .andExpect(jsonPath("$.email").value("existing@example.com"));

        verify(userService, times(1)).getUserById(eq(userId));
    }

    @Test
    @DisplayName("Получение всех пользователей — успешный ответ")
    void getAllUser_Should_Return_All_Users_Test() throws Exception {
        // Given
        UserResponseDto user1 = new UserResponseDto();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@example.com");

        UserResponseDto user2 = new UserResponseDto();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@example.com");

        List<UserResponseDto> users = List.of(user1, user2);

        when(userService.getAllUser()).thenReturn(List.of(new User(), new User()));
        when(userService.getListUserResponseDto(any(List.class))).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("User 2"));

        verify(userService, times(1)).getAllUser();
    }

    @Test
    @DisplayName("Обновление пользователя по ID — успешный ответ")
    void updateUserById_Should_Return_Updated_User_Test() throws Exception {
        // Given
        long userId = 1L;
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Updated Name");
        userUpdateDto.setEmail("updated@example.com");

        UserResponseDto updatedUserResponseDto = new UserResponseDto();
        updatedUserResponseDto.setId(userId);
        updatedUserResponseDto.setName("Updated Name");
        updatedUserResponseDto.setEmail("updated@example.com");

        when(userService.updateUserById(eq(userId), any(UserUpdateDto.class))).thenReturn(new User());
        when(userService.getUserResponseDto(any(User.class))).thenReturn(updatedUserResponseDto);

        // When & Then
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService, times(1)).updateUserById(eq(userId), any(UserUpdateDto.class));
    }


    @Test
    @DisplayName("Удаление пользователя по ID — успешный ответ (No Content)")
    void deleteUserById_Should_Return_No_Content_Test() throws Exception {
        // Given
        long userId = 1L;

        doNothing().when(userService).deleteUserById(eq(userId));

        // When & Then
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(eq(userId));
    }
}
