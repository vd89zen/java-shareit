package ru.practicum.shareit.gateway.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Json тесты для NewBookingDto")
class NewBookingDtoJsonTest {

    private final ObjectMapper objectMapper;
    private final JacksonTester<NewBookingDto> json;

    private NewBookingDto createNewBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        NewBookingDto bookingDto = new NewBookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        return bookingDto;
    }

    @Test
    @DisplayName("Сериализация NewBookingDto в JSON — все поля заполнены")
    void serialize_NewBookingDto_With_All_Fields_Filled_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        NewBookingDto newBookingDto = createNewBookingDto(
                123L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        // When
        JsonContent<NewBookingDto> result = json.write(newBookingDto);

        // Then
        assertThat(result)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(123);

        assertThat(result)
                .extractingJsonPathStringValue("$.start")
                .isNotNull();

        assertThat(result)
                .extractingJsonPathStringValue("$.end")
                .isNotNull();
    }

    @Test
    @DisplayName("Десериализация JSON в NewBookingDto — все поля присутствуют")
    void deserialize_Json_With_All_Fields_Present_Should_Create_NewBookingDto_Test() throws Exception {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        NewBookingDto dto = createNewBookingDto(456L, start, end);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isEqualTo(456L);
        assertThat(result.getStart()).isEqualTo(start);
        assertThat(result.getEnd()).isEqualTo(end);
    }

    @Test
    @DisplayName("Десериализация с itemId = 0 — должно создать объект с itemId = 0")
    void deserialize_Json_With_ItemId_Zero_Should_Create_Object_Test() throws Exception {
        // Given
        NewBookingDto dto = createNewBookingDto(
                0L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isZero();
    }

    @Test
    @DisplayName("Десериализация с прошлыми датами — должно создать объект (валидация не срабатывает на уровне JSON)")
    void deserialize_Json_With_Past_Dates_Should_Create_Object_Test() throws Exception {
        // Given
        LocalDateTime pastStart = LocalDateTime.now().minusDays(2);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);

        NewBookingDto dto = createNewBookingDto(789L, pastStart, pastEnd);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isEqualTo(789L);
        assertThat(result.getStart()).isEqualTo(pastStart);
        assertThat(result.getEnd()).isEqualTo(pastEnd);
    }

    @Test
    @DisplayName("Десериализация с start = null — должно создать объект с null start")
    void deserialize_Json_With_Start_Null_Should_Create_Object_With_Null_Start_Test() throws Exception {
        // Given
        NewBookingDto dto = createNewBookingDto(
                111L,
                null,
                LocalDateTime.now().plusDays(1)
        );
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isEqualTo(111L);
        assertThat(result.getStart()).isNull();
        assertThat(result.getEnd()).isNotNull();
    }

    @Test
    @DisplayName("Десериализация с end = null — должно создать объект с null end")
    void deserialize_Json_With_End_Null_Should_Create_Object_With_Null_End_Test() throws Exception {
        // Given
        NewBookingDto dto = createNewBookingDto(
                222L,
                LocalDateTime.now().plusDays(1),
                null
        );
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isEqualTo(222L);
        assertThat(result.getStart()).isNotNull();
        assertThat(result.getEnd()).isNull();
    }

    @Test
    @DisplayName("Сериализация объекта с null датами — поля start и end должны быть null в JSON")
    void serialize_NewBookingDto_With_Null_Dates_Should_Include_Null_In_Json_Test() throws Exception {
        // Given
        NewBookingDto newBookingDto = createNewBookingDto(333L, null, null);

        // When
        JsonContent<NewBookingDto> result = json.write(newBookingDto);

        // Then
        assertThat(result)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(333);

        assertThat(result)
                .extractingJsonPathValue("$.start")
                .isNull();

        assertThat(result)
                .extractingJsonPathValue("$.end")
                .isNull();
    }

    @Test
    @DisplayName("Десериализация с одинаковой датой start и end — должно создать объект")
    void deserialize_Json_With_Same_Start_And_End_Dates_Should_Create_Object_Test() throws Exception {
        // Given
        LocalDateTime sameDate = LocalDateTime.now().plusDays(3);

        NewBookingDto dto = createNewBookingDto(444L, sameDate, sameDate);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isEqualTo(444L);
        assertThat(result.getStart()).isEqualTo(sameDate);
        assertThat(result.getEnd()).isEqualTo(sameDate);
    }

    @Test
    @DisplayName("Десериализация с end раньше start — должно создать объект (валидация позже)")
    void deserialize_Json_With_End_Before_Start_Should_Create_Object_Test() throws Exception {
        // Given
        LocalDateTime laterStart = LocalDateTime.now().plusDays(2);
        LocalDateTime earlierEnd = LocalDateTime.now().plusDays(1);

        NewBookingDto dto = createNewBookingDto(555L, laterStart, earlierEnd);
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isEqualTo(555L);
        assertThat(result.getStart()).isEqualTo(laterStart);
        assertThat(result.getEnd()).isEqualTo(earlierEnd);
    }

    @Test
    @DisplayName("Сериализация с точной временной меткой — проверка через десериализацию")
    void serialize_NewBookingDto_With_Nanoseconds_Precision_Via_Deserialization_Test() throws Exception {
        // Given
        LocalDateTime preciseStart = LocalDateTime.of(
                2024, 3, 15, 14, 30, 45, 123456789);
        LocalDateTime preciseEnd = LocalDateTime.of(
                2024, 3, 16, 10, 15, 30, 987654321);

        NewBookingDto originalDto = createNewBookingDto(666L, preciseStart, preciseEnd);

        // When
        String jsonString = objectMapper.writeValueAsString(originalDto);
        NewBookingDto resultDto = json.parseObject(jsonString);

        // Then
        assertThat(resultDto.getItemId()).isEqualTo(666L);
        assertThat(resultDto.getStart()).isEqualTo(preciseStart);
        assertThat(resultDto.getEnd()).isEqualTo(preciseEnd);
    }

    @Test
    @DisplayName("Десериализация даты в стандартном формате ISO")
    void deserialize_Json_With_Iso_Date_Format_Should_Process_Correctly_Test() throws Exception {
        // Given
        String isoStart = "2024-12-25T10:30:00";
        String isoEnd = "2024-12-30T18:45:00";

        NewBookingDto dto = createNewBookingDto(
                777L,
                LocalDateTime.parse(isoStart),
                LocalDateTime.parse(isoEnd)
        );
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isEqualTo(777L);
        assertThat(result.getStart())
                .isEqualTo(LocalDateTime.parse(isoStart));
        assertThat(result.getEnd())
                .isEqualTo(LocalDateTime.parse(isoEnd));
    }

    @Test
    @DisplayName("Десериализация с нулевым itemId — должно корректно обработать")
    void deserialize_Json_With_Zero_ItemId_Should_Create_Object_Test() throws Exception {
        // Given
        NewBookingDto dto = createNewBookingDto(
                0L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3)
        );
        String jsonString = objectMapper.writeValueAsString(dto);

        // When
        NewBookingDto result = json.parseObject(jsonString);

        // Then
        assertThat(result.getItemId()).isZero();
        assertThat(result.getStart()).isNotNull();
        assertThat(result.getEnd()).isNotNull();
    }

    @Test
    @DisplayName("Сериализация объекта с максимально возможным itemId")
    void serialize_NewBookingDto_With_Max_ItemId_Should_Produce_Valid_Json_Test() throws Exception {
        // Given
        NewBookingDto newBookingDto = createNewBookingDto(
                Long.MAX_VALUE,
                LocalDateTime.now().plusYears(1),
                LocalDateTime.now().plusYears(2)
        );

        // When
        JsonContent<NewBookingDto> result = json.write(newBookingDto);

        // Then
        assertThat(result)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(Long.MAX_VALUE);
    }
}