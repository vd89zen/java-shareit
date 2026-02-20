package ru.practicum.shareit.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.user.dto.NewUserDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Тесты для UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание пользователя — успешный ответ")
    void createUser_Should_Return_Created_Test() throws Exception {
        // Given
        NewUserDto newUserDto = new NewUserDto("Test User", "test@example.com");

        when(userClient.createUser(any(NewUserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).createUser(any(NewUserDto.class));
    }

    @Test
    @DisplayName("Получение пользователя по ID — успешный ответ")
    void getUserById_Should_Return_User_Test() throws Exception {
        // Given
        long userId = 1L;
        when(userClient.findUserById(userId))
                .thenReturn(ResponseEntity.ok().body("User data"));

        // When & Then
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User data"));

        verify(userClient, times(1)).findUserById(userId);
    }

    @Test
    @DisplayName("Получение всех пользователей — успешный ответ")
    void getAllUsers_Should_Return_All_Users_Test() throws Exception {
        // Given
        when(userClient.findAllUser())
                .thenReturn(ResponseEntity.ok().body("[\"User1\", \"User2\"]"));

        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"User1\", \"User2\"]"));

        verify(userClient, times(1)).findAllUser();
    }

    @Test
    @DisplayName("Обновление пользователя — успешный ответ")
    void updateUser_Should_Return_Updated_User_Test() throws Exception {
        // Given
        long userId = 1L;
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Updated Name");

        when(userClient.updateUser(eq(userId), any(UserUpdateDto.class)))
                .thenReturn(ResponseEntity.ok().body("Updated user"));

        // When & Then
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated user"));

        verify(userClient, times(1)).updateUser(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("Удаление пользователя по ID — успешный ответ (No Content)")
    void deleteUserById_Should_Return_No_Content_Test() throws Exception {
        // Given
        long userId = 1L;
        when(userClient.deleteUserById(userId))
                .thenReturn(ResponseEntity.noContent().build());

        // When & Then
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userClient, times(1)).deleteUserById(userId);
    }
}
