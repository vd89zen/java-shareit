package ru.practicum.shareit.gateway.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Json тесты для NewUserDto")
class NewUserDtoJsonTest {

    private final ObjectMapper objectMapper;
    private final JacksonTester<NewUserDto> json;

    @Test
    @DisplayName("Сериализация NewUserDto в JSON — корректные данные")
    void serialize_NewUserDto_With_Valid_Data_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Иван Иванов");
        newUserDto.setEmail("ivan.ivanov@example.com");

        // When
        JsonContent<NewUserDto> result = json.write(newUserDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Иван Иванов");

        assertThat(result)
                .extractingJsonPathStringValue("$.email")
                .isEqualTo("ivan.ivanov@example.com");
    }

    @Test
    @DisplayName("Десериализация JSON в NewUserDto — корректные данные")
    void deserialize_Json_With_Valid_Data_Should_Create_NewUserDto_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "name": "Петр Петров",
            "email": "petr.petrov@example.com"
            }
            """;

        // When
        NewUserDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Петр Петров");
        assertThat(result.getEmail()).isEqualTo("petr.petrov@example.com");
    }

    @Test
    @DisplayName("Десериализация неполного JSON — отсутствует имя")
    void deserialize_Incomplete_Json_Without_Name_Should_Create_Object_With_Null_Name_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "email": "test@example.com"
            }
            """;

        // When
        NewUserDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Десериализация неполного JSON — отсутствует email")
    void deserialize_Incomplete_Json_Without_Email_Should_Create_Object_With_Null_Email_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "name": "Алексей Алексеев"
            }
            """;

        // When
        NewUserDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Алексей Алексеев");
        assertThat(result.getEmail()).isNull();
    }

    @Test
    @DisplayName("Десериализация с пробелами в имени — должно пройти")
    void deserialize_Json_With_Spaces_In_Name_Should_Be_Accepted_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "name": "   ",
            "email": "valid@example.com"
            }
            """;

        // When
        NewUserDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("   ");
        assertThat(result.getEmail()).isEqualTo("valid@example.com");
    }

    @Test
    @DisplayName("Десериализация некорректного email — должно создать объект с полем email")
    void deserialize_Json_With_Invalid_Email_Format_Should_Create_Object_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "name": "Ольга Орлова",
            "email": "invalid-email"
            }
            """;

        // When
        NewUserDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Ольга Орлова");
        assertThat(result.getEmail()).isEqualTo("invalid-email");
    }
}


