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
@DisplayName("Json тесты для NewItemDto")
class NewItemDtoJsonTest {

    private final ObjectMapper objectMapper;
    private final JacksonTester<NewItemDto> json;

    @Test
    @DisplayName("Сериализация NewItemDto в JSON — все поля заполнены")
    void serialize_NewItemDto_With_All_Fields_Filled_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Дрель электрическая");
        newItemDto.setDescription("Мощная дрель для сверления отверстий в бетоне");
        newItemDto.setAvailable(true);
        newItemDto.setRequestId(123L);

        // When
        JsonContent<NewItemDto> result = json.write(newItemDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Дрель электрическая");

        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("Мощная дрель для сверления отверстий в бетоне");

        assertThat(result)
                .extractingJsonPathBooleanValue("$.available")
                .isTrue();

        assertThat(result)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(123);
    }

    @Test
    @DisplayName("Десериализация JSON в NewItemDto — все поля присутствуют")
    void deserialize_Json_With_All_Fields_Present_Should_Create_NewItemDto_Test() throws Exception {
        // Given
        NewItemDto dto = new NewItemDto();
        dto.setName("Пылесос робот");
        dto.setDescription("Автоматический пылесос с функцией влажной уборки");
        dto.setAvailable(false);
        dto.setRequestId(456L);
        String jsonString = objectMapper.writeValueAsString(dto);//другой подход к формированию jsonstring

        // When
        NewItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Пылесос робот");
        assertThat(result.getDescription()).isEqualTo("Автоматический пылесос с функцией влажной уборки");
        assertThat(result.getAvailable()).isFalse();
        assertThat(result.getRequestId()).isEqualTo(456L);
    }

    @Test
    @DisplayName("Десериализация JSON — отсутствует requestId (опциональное поле)")
    void deserialize_Json_Without_RequestId_Should_Create_Object_With_Null_RequestId_Test() throws Exception {
        // Given
        NewItemDto dto = new NewItemDto();
        dto.setName("Фен");
        dto.setDescription("Профессиональный фен для волос");
        dto.setAvailable(true);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Фен");
        assertThat(result.getDescription()).isEqualTo("Профессиональный фен для волос");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    @DisplayName("Сериализация с requestId = null — должно корректно обработать")
    void serialize_NewItemDto_With_RequestId_Null_Should_Include_Null_In_Json_Test() throws Exception {
        // Given
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Ноутбук");
        newItemDto.setDescription("Игровой ноутбук с мощной видеокартой");
        newItemDto.setAvailable(false);
        newItemDto.setRequestId(null);

        // When
        JsonContent<NewItemDto> result = json.write(newItemDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Ноутбук");

        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("Игровой ноутбук с мощной видеокартой");

        assertThat(result)
                .extractingJsonPathBooleanValue("$.available")
                .isFalse();

        assertThat(result)
                .extractingJsonPathValue("$.requestId")
                .isNull();
    }

    @Test
    @DisplayName("Десериализация очень длинного названия (>128 символов) — должно создать объект")
    void deserialize_Json_With_Very_Long_Name_Should_Create_Object_Test() throws Exception {
        // Given
        String veryLongName = "X".repeat(150); // 150 символов (превышает лимит 128)
        NewItemDto dto = new NewItemDto();
        dto.setName(veryLongName);
        dto.setDescription("Короткое описание");
        dto.setAvailable(true);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName().length()).isEqualTo(150);
        assertThat(result.getName()).isEqualTo(veryLongName);
        assertThat(result.getDescription()).isEqualTo("Короткое описание");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Десериализация очень длинного описания (>1024 символов) — должно создать объект")
    void deserialize_Json_With_Very_Long_Description_Should_Create_Object_Test() throws Exception {
        // Given
        String veryLongDescription = "A".repeat(1100); // 1100 символов
        NewItemDto dto = new NewItemDto();
        dto.setName("Вещь");
        dto.setDescription(veryLongDescription);
        dto.setAvailable(false);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getDescription().length()).isEqualTo(1100);
        assertThat(result.getDescription()).isEqualTo(veryLongDescription);
        assertThat(result.getName()).isEqualTo("Вещь");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Десериализация с пустым названием — должно создать объект с пустым полем")
    void deserialize_Json_With_Empty_Name_Should_Create_Object_Test() throws Exception {
        // Given
        NewItemDto dto = new NewItemDto();
        dto.setName("");
        dto.setDescription("Нормальное описание");
        dto.setAvailable(true);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("");
        assertThat(result.getDescription()).isEqualTo("Нормальное описание");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Десериализация без поля available — должно создать объект с null available")
    void deserialize_Json_Without_Available_Field_Should_Create_Object_With_Null_Available_Test() throws Exception {
        // Given
        NewItemDto dto = new NewItemDto();
        dto.setName("Вещь");
        dto.setDescription("Описание без доступности");
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Вещь");
        assertThat(result.getDescription()).isEqualTo("Описание без доступности");
        assertThat(result.getAvailable()).isNull();
    }

    @Test
    @DisplayName("Десериализация с пробелами в названии — должно создать объект")
    void deserialize_Json_With_Spaces_In_Name_Should_Create_Object_Test() throws Exception {
        // Given
        NewItemDto dto = new NewItemDto();
        dto.setName("   ");
        dto.setDescription("Описание с пробелами в названии");
        dto.setAvailable(true);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("   ");
        assertThat(result.getDescription()).isEqualTo("Описание с пробелами в названии");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Десериализация с null в описании — должно создать объект с null description")
    void deserialize_Json_With_Null_Description_Should_Create_Object_With_Null_Description_Test() throws Exception {
        // Given
        NewItemDto dto = new NewItemDto();
        dto.setName("Вещь с null описанием");
        dto.setDescription(null);
        dto.setAvailable(false);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewItemDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getName()).isEqualTo("Вещь с null описанием");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Сериализация объекта с null полями — все поля должны присутствовать в JSON")
    void serialize_NewItemDto_With_Null_Fields_Should_Include_All_Fields_In_Json_Test() throws Exception {
        // Given
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Объект с null полями");
        newItemDto.setDescription(null);
        newItemDto.setAvailable(null);
        newItemDto.setRequestId(null);

        // When
        JsonContent<NewItemDto> result = json.write(newItemDto);

        // Then
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Объект с null полями");

        assertThat(result)
                .extractingJsonPathValue("$.description")
                .isNull();

        assertThat(result)
                .extractingJsonPathValue("$.available")
                .isNull();

        assertThat(result)
                .extractingJsonPathValue("$.requestId")
                .isNull();
    }
}
