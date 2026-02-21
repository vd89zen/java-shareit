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

    private static final long USER_ID = 1L;

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
        when(userClient.findUserById(USER_ID))
                .thenReturn(ResponseEntity.ok().body("User data"));

        // When & Then
        mockMvc.perform(get("/users/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("User data"));

        verify(userClient, times(1)).findUserById(USER_ID);
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
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Updated Name");

        when(userClient.updateUser(eq(USER_ID), any(UserUpdateDto.class)))
                .thenReturn(ResponseEntity.ok().body("Updated user"));

        // When & Then
        mockMvc.perform(patch("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated user"));

        verify(userClient, times(1)).updateUser(eq(USER_ID), any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("Удаление пользователя по ID — успешный ответ (No Content)")
    void deleteUserById_Should_Return_No_Content_Test() throws Exception {
        // Given
        when(userClient.deleteUserById(USER_ID))
                .thenReturn(ResponseEntity.noContent().build());

        // When & Then
        mockMvc.perform(delete("/users/{userId}", USER_ID))
                .andExpect(status().isNoContent());

        verify(userClient, times(1)).deleteUserById(USER_ID);
    }
}
