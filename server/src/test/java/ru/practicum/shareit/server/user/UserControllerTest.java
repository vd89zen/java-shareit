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

    private static final long USER_ID = 1L;

    private NewUserDto createNewUserDto(String name, String email) {
        NewUserDto userDto = new NewUserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    private UserResponseDto createUserResponseDto(long id, String name, String email) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(id);
        userResponseDto.setName(name);
        userResponseDto.setEmail(email);
        return userResponseDto;
    }

    private UserUpdateDto createUserUpdateDto(String name, String email) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(name);
        userUpdateDto.setEmail(email);
        return userUpdateDto;
    }

    private List<UserResponseDto> createUserResponseDtoList() {
        UserResponseDto user1 = createUserResponseDto(1L, "User 1", "user1@example.com");
        UserResponseDto user2 = createUserResponseDto(2L, "User 2", "user2@example.com");
        return List.of(user1, user2);
    }

    @Test
    @DisplayName("Создание пользователя — успешный ответ (CREATED)")
    void createUser_Should_Return_Created_Test() throws Exception {
        // Given
        NewUserDto newUserDto = createNewUserDto("Test User", "test@example.com");

        UserResponseDto userResponseDto = createUserResponseDto(
                USER_ID,
                "Test User",
                "test@example.com"
        );

        when(userService.createUser(any(NewUserDto.class))).thenReturn(new User());
        when(userService.getUserResponseDto(any(User.class))).thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).createUser(any(NewUserDto.class));
    }

    @Test
    @DisplayName("Получение пользователя по ID — успешный ответ")
    void getUserById_Should_Return_User_Test() throws Exception {
        // Given
        UserResponseDto userResponseDto = createUserResponseDto(
                USER_ID,
                "Existing User",
                "existing@example.com"
        );

        when(userService.getUserById(eq(USER_ID))).thenReturn(new User());
        when(userService.getUserResponseDto(any(User.class))).thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(get("/users/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value("Existing User"))
                .andExpect(jsonPath("$.email").value("existing@example.com"));

        verify(userService, times(1)).getUserById(eq(USER_ID));
    }

    @Test
    @DisplayName("Получение всех пользователей — успешный ответ")
    void getAllUser_Should_Return_All_Users_Test() throws Exception {
        // Given
        List<UserResponseDto> users = createUserResponseDtoList();

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
        UserUpdateDto userUpdateDto = createUserUpdateDto("Updated Name", "updated@example.com");

        UserResponseDto updatedUserResponseDto = createUserResponseDto(
                USER_ID,
                "Updated Name",
                "updated@example.com"
        );

        when(userService.updateUserById(eq(USER_ID), any(UserUpdateDto.class)))
                .thenReturn(new User());
        when(userService.getUserResponseDto(any(User.class)))
                .thenReturn(updatedUserResponseDto);

        // When & Then
        mockMvc.perform(patch("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService, times(1)).updateUserById(eq(USER_ID), any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("Удаление пользователя по ID — успешный ответ (No Content)")
    void deleteUserById_Should_Return_No_Content_Test() throws Exception {
        // Given
        doNothing().when(userService).deleteUserById(eq(USER_ID));

        // When & Then
        mockMvc.perform(delete("/users/{userId}", USER_ID))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(eq(USER_ID));
    }
}

