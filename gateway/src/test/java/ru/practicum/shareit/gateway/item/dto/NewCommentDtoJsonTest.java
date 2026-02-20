package ru.practicum.shareit.gateway.item.dto;

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
@DisplayName("Json тесты для NewCommentDto")
class NewCommentDtoJsonTest {

    private final ObjectMapper objectMapper;
    private final JacksonTester<NewCommentDto> json;

    @Test
    @DisplayName("Сериализация NewCommentDto в JSON — текст комментария заполнен")
    void serialize_NewCommentDto_With_Text_Filled_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("Отличный товар, всем рекомендую!");

        // When
        JsonContent<NewCommentDto> result = json.write(newCommentDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.text")
                .isEqualTo("Отличный товар, всем рекомендую!");
    }

    @Test
    @DisplayName("Десериализация JSON в NewCommentDto — текст присутствует")
    void deserialize_Json_With_Text_Present_Should_Create_NewCommentDto_Test() throws Exception {
        // Given
        NewCommentDto dto = new NewCommentDto();
        dto.setText("Полезный отзыв о товаре");
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewCommentDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getText()).isEqualTo("Полезный отзыв о товаре");
    }

    @Test
    @DisplayName("Десериализация очень длинного текста (>1024 символов) — должно создать объект")
    void deserialize_Json_With_Very_Long_Text_Should_Create_Object_Test() throws Exception {
        // Given
        String veryLongText = "A".repeat(1100);
        NewCommentDto dto = new NewCommentDto();
        dto.setText(veryLongText);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewCommentDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getText().length()).isEqualTo(1100);
        assertThat(result.getText()).isEqualTo(veryLongText);
    }

    @Test
    @DisplayName("Десериализация с пустым текстом — должно создать объект с пустой строкой")
    void deserialize_Json_With_Empty_Text_Should_Create_Object_Test() throws Exception {
        // Given
        NewCommentDto dto = new NewCommentDto();
        dto.setText("");
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewCommentDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getText()).isEqualTo("");
    }

    @Test
    @DisplayName("Десериализация с пробелами в тексте — должно создать объект со строкой из пробелов")
    void deserialize_Json_With_Spaces_In_Text_Should_Create_Object_Test() throws Exception {
        // Given
        NewCommentDto dto = new NewCommentDto();
        dto.setText("   ");
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewCommentDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getText()).isEqualTo("   ");
    }

    @Test
    @DisplayName("Десериализация с null текстом — должно создать объект с null text")
    void deserialize_Json_With_Null_Text_Should_Create_Object_With_Null_Text_Test() throws Exception {
        // Given
        NewCommentDto dto = new NewCommentDto();
        dto.setText(null);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewCommentDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getText()).isNull();
    }

    @Test
    @DisplayName("Сериализация объекта с null текстом — поле text должно быть null в JSON")
    void serialize_NewCommentDto_With_Null_Text_Should_Include_Null_In_Json_Test() throws Exception {
        // Given
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText(null);

        // When
        JsonContent<NewCommentDto> result = json.write(newCommentDto);

        // Then
        assertThat(result)
                .extractingJsonPathValue("$.text")
                .isNull();
    }

    @Test
    @DisplayName("Десериализация минимального текста (1 символ) — должно создать объект")
    void deserialize_Json_With_Minimal_Text_Should_Create_Object_Test() throws Exception {
        // Given
        NewCommentDto dto = new NewCommentDto();
        dto.setText("X");
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewCommentDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getText()).isEqualTo("X");
        assertThat(result.getText().length()).isEqualTo(1);
    }

    @Test
    @DisplayName("Десериализация текста с спецсимволами — должно корректно обработать")
    void deserialize_Json_With_Special_Characters_In_Text_Should_Process_Correctly_Test() throws Exception {
        // Given
        String originalText = "Отзыв с \"кавычками\", \\\\обратными слешами и \\nпереносами";
        NewCommentDto dto = new NewCommentDto();
        dto.setText(originalText);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewCommentDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getText()).isEqualTo(originalText);
    }
}
