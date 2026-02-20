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

    @Test
    @DisplayName("createUser — должен создать нового пользователя с уникальными данными")
    void createUser_ShouldCreateNewUserWithUniqueData() {
        // Given
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Test User");
        newUserDto.setEmail("test@user.com");

        // When
        User createdUser = userService.createUser(newUserDto);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("Test User");
        assertThat(createdUser.getEmail()).isEqualTo("test@user.com");

        User foundInDb = userRepository.findById(createdUser.getId())
                .orElse(null);
        assertThat(foundInDb).isNotNull();
        assertThat(foundInDb.getName()).isEqualTo("Test User");
        assertThat(foundInDb.getEmail()).isEqualTo("test@user.com");
    }

    @Test
    @DisplayName("createUser — должен выбросить ValidationException при дублировании email")
    void createUser_ShouldThrowValidationExceptionWhenEmailDuplicate() {
        // Given
        NewUserDto firstUserDto = new NewUserDto();
        firstUserDto.setName("First User");
        firstUserDto.setEmail("duplicate@email.com");
        userService.createUser(firstUserDto);

        NewUserDto secondUserDto = new NewUserDto();
        secondUserDto.setName("Second User");
        secondUserDto.setEmail("duplicate@email.com");

        // When & Then
        assertThatThrownBy(() -> userService.createUser(secondUserDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Данный email уже используется");
    }
}
