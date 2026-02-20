package ru.practicum.shareit.gateway.request.dto;

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
@DisplayName("Json тесты для NewRequestItemDto")
class NewRequestItemDtoJsonTest {

    private final ObjectMapper objectMapper;
    private final JacksonTester<NewRequestItemDto> json;

    @Test
    @DisplayName("Сериализация NewRequestItemDto в JSON — корректное описание")
    void serialize_NewRequestItemDto_With_Valid_Description_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        NewRequestItemDto newRequestItemDto = new NewRequestItemDto();
        newRequestItemDto.setDescription("Нужно арендовать дрель на выходные");

        // When
        JsonContent<NewRequestItemDto> result = json.write(newRequestItemDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужно арендовать дрель на выходные");
    }

    @Test
    @DisplayName("Десериализация JSON в NewRequestItemDto — корректные данные")
    void deserialize_Json_With_Valid_Data_Should_Create_NewRequestItemDto_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "description": "Требуется пылесос для уборки"
            }
            """;

        // When
        NewRequestItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getDescription()).isEqualTo("Требуется пылесос для уборки");
    }

    @Test
    @DisplayName("Десериализация JSON — описание ровно 1024 символа")
    void deserialize_Json_With_Description_Exactly_1024_Chars_Should_Be_Accepted_Test() throws Exception {
        // Given
        String longDescription = "А".repeat(1024);

        String jsonString = String.format("""
                {
                "description": "%s"
                }
                """, longDescription);

        // When
        NewRequestItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getDescription().length()).isEqualTo(1024);
        assertThat(result.getDescription()).isEqualTo(longDescription.toString());
    }

    @Test
    @DisplayName("Десериализация JSON — отсутствует поле description")
    void deserialize_Json_Without_Description_Field_Should_Create_Object_With_Null_Description_Test() throws Exception {
        // Given
        String jsonString = "{}";

        // When
        NewRequestItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getDescription()).isNull();
    }

    @Test
    @DisplayName("Десериализация с пустым описанием — должно создать объект с пустым полем")
    void deserialize_Json_With_Empty_Description_Should_Create_Object_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "description": ""
            }
            """;

        // When
        NewRequestItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getDescription()).isEqualTo("");
    }

    @Test
    @DisplayName("Десериализация с пробелами в описании — должно создать объект")
    void deserialize_Json_With_Only_Spaces_In_Description_Should_Create_Object_Test() throws Exception {
        // Given
        String jsonString = """
            {
            "description": "   "
            }
            """;

        // When
        NewRequestItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getDescription()).isEqualTo("   ");
    }

    @Test
    @DisplayName("Десериализация очень длинного описания (>1024 символов) — должно создать объект")
    void deserialize_Json_With_Very_Long_Description_Should_Create_Object_Test() throws Exception {
        // Given
        String veryLongDescription = "X".repeat(1100);

        String jsonString = String.format("""
                {
                "description": "%s"
                }
                """, veryLongDescription);

        // When
        NewRequestItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getDescription().length()).isEqualTo(1100);
        assertThat(result.getDescription()).isEqualTo(veryLongDescription.toString());
    }
}
