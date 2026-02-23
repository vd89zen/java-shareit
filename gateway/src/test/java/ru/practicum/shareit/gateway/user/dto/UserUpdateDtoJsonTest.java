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
@DisplayName("Json тесты для UserUpdateDto")
class UserUpdateDtoJsonTest {

    private final ObjectMapper objectMapper;
    private final JacksonTester<UserUpdateDto> json;

    @Test
    @DisplayName("Сериализация UserUpdateDto в JSON — оба поля заполнены")
    void serialize_UserUpdateDto_With_Both_Fields_Filled_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Иван Иванов");
        userUpdateDto.setEmail("ivan.ivanov@example.com");

        // When
        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Иван Иванов");

        assertThat(result)
                .extractingJsonPathStringValue("$.email")
                .isEqualTo("ivan.ivanov@example.com");
    }

    @Test
    @DisplayName("Сериализация UserUpdateDto — все поля null")
    void serialize_UserUpdateDto_With_All_Fields_Null_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        UserUpdateDto userUpdateDto = new UserUpdateDto(); // все поля по умолчанию null

        // When
        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isNull();

        assertThat(result)
                .extractingJsonPathStringValue("$.email")
                .isNull();
    }

    @Test
    @DisplayName("Десериализация JSON в UserUpdateDto — оба поля присутствуют")
    void deserialize_Json_With_Both_Fields_Present_Should_Create_UserUpdateDto_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "name": "Петр Петров",
            "email": "petr.petrov@example.com"
            }
            """;

        // When
        UserUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Петр Петров");
        assertThat(result.getEmail()).isEqualTo("petr.petrov@example.com");
    }

    @Test
    @DisplayName("Десериализация JSON — отсутствует поле name")
    void deserialize_Json_Without_Name_Field_Should_Create_Object_With_Null_Name_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "email": "test@example.com"
            }
            """;

        // When
        UserUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Десериализация JSON — отсутствует поле email")
    void deserialize_Json_Without_Email_Field_Should_Create_Object_With_Null_Email_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "name": "Алексей Алексеев"
            }
            """;

        // Then
        UserUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Алексей Алексеев");
        assertThat(result.getEmail()).isNull();
    }

    @Test
    @DisplayName("Десериализация JSON — оба поля отсутствуют")
    void deserialize_Json_Without_Any_Fields_Should_Create_Object_With_All_Null_Test() throws Exception {
        // Given
        String jsonString = "{}";

        // When
        UserUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }

    @Test
    @DisplayName("Десериализация с пробелами в name — должно создать объект с полем name")
    void deserialize_Json_With_Only_Spaces_In_Name_Should_Create_Object_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "name": "   ",
            "email": "valid@example.com"
            }
            """;

        // When
        UserUpdateDto result = json.parseObject(jsonString);

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
        UserUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Ольга Орлова");
        assertThat(result.getEmail()).isEqualTo("invalid-email");
    }
}
