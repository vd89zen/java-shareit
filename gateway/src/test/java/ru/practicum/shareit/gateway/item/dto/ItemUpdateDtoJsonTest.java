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
@DisplayName("Json тесты для ItemUpdateDto")
class ItemUpdateDtoJsonTest {

    private final ObjectMapper objectMapper;
    private final JacksonTester<ItemUpdateDto> json;

    @Test
    @DisplayName("Сериализация ItemUpdateDto в JSON — все поля заполнены")
    void serialize_ItemUpdateDto_With_All_Fields_Filled_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Обновлённое название");
        itemUpdateDto.setDescription("Обновлённое описание");
        itemUpdateDto.setAvailable(true);

        // When
        JsonContent<ItemUpdateDto> result = json.write(itemUpdateDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Обновлённое название");

        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("Обновлённое описание");

        assertThat(result)
                .extractingJsonPathBooleanValue("$.available")
                .isTrue();
    }

    @Test
    @DisplayName("Десериализация JSON в ItemUpdateDto — все поля присутствуют")
    void deserialize_Json_With_All_Fields_Present_Should_Create_ItemUpdateDto_Test() throws Exception {
        // Given
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Новое название");
        dto.setDescription("Новое описание");
        dto.setAvailable(false);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        ItemUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Новое название");
        assertThat(result.getDescription()).isEqualTo("Новое описание");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Десериализация JSON — отсутствует поле name")
    void deserialize_Json_Without_Name_Field_Should_Create_Object_With_Null_Name_Test() throws Exception {
        // Given
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName(null);
        dto.setDescription("Описание без названия");
        dto.setAvailable(true);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        ItemUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isNull();
        assertThat(result.getDescription()).isEqualTo("Описание без названия");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Десериализация с name = null — должно создать объект с null name")
    void deserialize_Json_With_Name_Null_Should_Create_Object_With_Null_Name_Test() throws Exception {
        // Given
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName(null);
        dto.setDescription("Нормальное описание");
        dto.setAvailable(false);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        ItemUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isNull();
        assertThat(result.getDescription()).isEqualTo("Нормальное описание");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Десериализация с description = null — должно создать объект с null description")
    void deserialize_Json_With_Description_Null_Should_Create_Object_With_Null_Description_Test() throws Exception {
        // Given
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Нормальное название");
        dto.setDescription(null);
        dto.setAvailable(true);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        ItemUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Нормальное название");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Десериализация с пустым name — должно создать объект с пустой строкой")
    void deserialize_Json_With_Empty_Name_Should_Create_Object_Test() throws Exception {
        // Given
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("");
        dto.setDescription("Описание с пустым названием");
        dto.setAvailable(true);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        ItemUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("");
        assertThat(result.getDescription()).isEqualTo("Описание с пустым названием");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Десериализация с пробелами в name — должно создать объект со строкой из пробелов")
    void deserialize_Json_With_Spaces_In_Name_Should_Create_Object_Test() throws Exception {
        // Given
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("   ");
        dto.setDescription("Описание с пробелами в названии");
        dto.setAvailable(false);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        ItemUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("   ");
        assertThat(result.getDescription()).isEqualTo("Описание с пробелами в названии");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Десериализация без поля available — должно создать объект с null available")
    void deserialize_Json_Without_Available_Field_Should_Create_Object_With_Null_Available_Test() throws Exception {
        // Given
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Вещь без доступности");
        dto.setDescription("Описание без поля available");
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        ItemUpdateDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Вещь без доступности");
        assertThat(result.getDescription()).isEqualTo("Описание без поля available");
        assertThat(result.getAvailable()).isNull();
    }

    @Test
    @DisplayName("Сериализация объекта с null полями — все поля должны присутствовать в JSON")
    void serialize_ItemUpdateDto_With_Null_Fields_Should_Include_All_Fields_In_Json_Test() throws Exception {
        // Given
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName(null);
        itemUpdateDto.setDescription(null);
        itemUpdateDto.setAvailable(null);

        // When
        JsonContent<ItemUpdateDto> result = json.write(itemUpdateDto);

        // Then
        assertThat(result)
                .extractingJsonPathValue("$.name")
                .isNull();

        assertThat(result)
                .extractingJsonPathValue("$.description")
                .isNull();

        assertThat(result)
                .extractingJsonPathValue("$.available")
                .isNull();
    }
}