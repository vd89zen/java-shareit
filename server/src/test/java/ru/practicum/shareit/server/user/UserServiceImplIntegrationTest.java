package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Интеграционные тесты для метода createUser сервиса UserServiceImpl")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private NewUserDto createNewUserDto(String name, String email) {
        return new NewUserDto(name, email);
    }

    @Test
    @DisplayName("createUser — должен создать нового пользователя с уникальными данными")
    void createUser_ShouldCreateNewUserWithUniqueData() {
        // Given
        String testName = "Test User";
        String testEmail = "test@user.com";
        NewUserDto newUserDto = createNewUserDto(testName, testEmail);

        // When
        User createdUser = userService.createUser(newUserDto);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo(testName);
        assertThat(createdUser.getEmail()).isEqualTo(testEmail);

        User foundInDb = userRepository.findById(createdUser.getId())
                .orElse(null);
        assertThat(foundInDb).isNotNull();
        assertThat(foundInDb.getName()).isEqualTo(testName);
        assertThat(foundInDb.getEmail()).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("createUser — должен выбросить ValidationException при дублировании email")
    void createUser_ShouldThrowValidationExceptionWhenEmailDuplicate() {
        // Given
        NewUserDto firstUserDto = createNewUserDto("First User", "duplicate@email.com");
        userService.createUser(firstUserDto);

        NewUserDto secondUserDto = createNewUserDto("Second User", "duplicate@email.com");

        // When & Then
        assertThatThrownBy(() -> userService.createUser(secondUserDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Данный email уже используется");
    }
}