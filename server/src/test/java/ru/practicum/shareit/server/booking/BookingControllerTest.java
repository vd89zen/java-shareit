package ru.practicum.shareit.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.dto.BookingState;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@DisplayName("Тесты для BookingController")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание брони — успешный ответ")
    void createBooking_Should_Return_Created_Test() throws Exception {
        // Given
        long userId = 1L;
        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setStart(LocalDateTime.now().plusDays(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        newBookingDto.setItemId(1L);

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .build();

        UserResponseDto bookerResponseDto = UserResponseDto.builder()
                .id(userId)
                .name("Test User")
                .email("user@test.com")
                .build();

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(itemResponseDto)
                .booker(bookerResponseDto)
                .status("WAITING")
                .build();

        when(bookingService.createBooking(eq(userId), any(NewBookingDto.class)))
                .thenReturn(new Booking());
        when(bookingService.getBookingResponseDto(any(Booking.class)))
                .thenReturn(bookingResponseDto);

        // When & Then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Test Item"))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.booker.name").value("Test User"));

        verify(bookingService, times(1)).createBooking(eq(userId), any(NewBookingDto.class));
    }

    @Test
    @DisplayName("Подтверждение/отказ брони — успешный ответ")
    void approveOrRejectBookingById_Should_Return_Updated_Booking_Test() throws Exception {
        // Given
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Booked Item")
                .description("Item for booking")
                .available(false)
                .build();

        UserResponseDto bookerResponseDto = UserResponseDto.builder()
                .id(2L)
                .name("Booker User")
                .email("booker@test.com")
                .build();

        BookingResponseDto updatedBookingResponseDto = BookingResponseDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(itemResponseDto)
                .booker(bookerResponseDto)
                .status(approved ? "APPROVED" : "REJECTED")
                .build();

        when(bookingService.approveOrRejectBookingById(eq(userId), eq(bookingId), eq(approved)))
                .thenReturn(new Booking());
        when(bookingService.getBookingResponseDto(any(Booking.class)))
                .thenReturn(updatedBookingResponseDto);

        // When & Then
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(2L));

        verify(bookingService, times(1)).approveOrRejectBookingById(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    @DisplayName("Получение брони по ID с проверкой доступа — успешный ответ")
    void getBookingByIdWithAccessCheck_Should_Return_Booking_Test() throws Exception {
        // Given
        long userId = 1L;
        long bookingId = 1L;

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Access Item")
                .description("Item with access")
                .available(true)
                .build();

        UserResponseDto bookerResponseDto = UserResponseDto.builder()
                .id(3L)
                .name("Access User")
                .email("access@test.com")
                .build();

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(itemResponseDto)
                .booker(bookerResponseDto)
                .status("WAITING")
                .build();

        when(bookingService.getBookingByIdWithAccessCheck(eq(userId), eq(bookingId)))
                .thenReturn(new Booking());
        when(bookingService.getBookingResponseDto(any(Booking.class)))
                .thenReturn(bookingResponseDto);

        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(3L))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists());

        verify(bookingService, times(1)).getBookingByIdWithAccessCheck(eq(userId), eq(bookingId));
    }

    @Test
    @DisplayName("Получение всех броней для арендатора — успешный ответ с пагинацией")
    void getBookingsForBooker_Should_Return_All_Bookings_With_Pagination_Test() throws Exception {
        // Given
        long userId = 1L;
        BookingState state = BookingState.ALL;
        int page = 0;
        int size = 10;

        ItemResponseDto item1 = ItemResponseDto.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming laptop")
                .available(true)
                .build();

        UserResponseDto booker1 = UserResponseDto.builder()
                .id(userId)
                .name("Ivan Ivanov")
                .email("ivan@test.com")
                .build();

        BookingResponseDto booking1 = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item1)
                .booker(booker1)
                .status("APPROVED")
                .build();

        ItemResponseDto item2 = ItemResponseDto.builder()
                .id(2L)
                .name("Phone")
                .description("Smartphone")
                .available(false)
                .build();

        UserResponseDto booker2 = UserResponseDto.builder()
                .id(2L)
                .name("Petr Petrov")
                .email("petr@test.com")
                .build();

        BookingResponseDto booking2 = BookingResponseDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(item2)
                .booker(booker2)
                .status("WAITING")
                .build();

        List<BookingResponseDto> bookings = List.of(booking1, booking2);

        when(bookingService.getBookingsForBooker(eq(userId), eq(state), eq(page), eq(size)))
                .thenReturn(List.of(new Booking(), new Booking()));
        when(bookingService.getListBookingResponseDto(any(List.class)))
                .thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Laptop"))
                .andExpect(jsonPath("$[0].booker.id").value(userId))
                .andExpect(jsonPath("$[0].booker.name").value("Ivan Ivanov"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("WAITING"))
                .andExpect(jsonPath("$[1].item.id").value(2L))
                .andExpect(jsonPath("$[1].item.name").value("Phone"))
                .andExpect(jsonPath("$[1].booker.id").value(2L))
                .andExpect(jsonPath("$[1].booker.name").value("Petr Petrov"))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[1].start").exists())
                .andExpect(jsonPath("$[1].end").exists());

        verify(bookingService, times(1)).getBookingsForBooker(eq(userId), eq(state), eq(page), eq(size));
    }

    @Test
    @DisplayName("Получение всех броней для владельца — успешный ответ с пагинацией")
    void getBookingsForOwner_Should_Return_All_Bookings_For_Owner_With_Pagination_Test() throws Exception {
        // Given
        long userId = 1L;
        BookingState state = BookingState.CURRENT;
        int page = 0;
        int size = 10;

        ItemResponseDto ownerItem1 = ItemResponseDto.builder()
                .id(3L)
                .name("Tablet")
                .description("Digital tablet")
                .available(true)
                .build();

        UserResponseDto ownerBooker1 = UserResponseDto.builder()
                .id(3L)
                .name("Sergey Sergeev")
                .email("sergey@test.com")
                .build();

        BookingResponseDto ownerBooking1 = BookingResponseDto.builder()
                .id(3L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(ownerItem1)
                .booker(ownerBooker1)
                .status("APPROVED")
                .build();

        ItemResponseDto ownerItem2 = ItemResponseDto.builder()
                .id(4L)
                .name("Camera")
                .description("DSLR camera")
                .available(false)
                .build();

        UserResponseDto ownerBooker2 = UserResponseDto.builder()
                .id(4L)
                .name("Anna Ananova")
                .email("anna@test.com")
                .build();

        BookingResponseDto ownerBooking2 = BookingResponseDto.builder()
                .id(4L)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(7))
                .item(ownerItem2)
                .booker(ownerBooker2)
                .status("WAITING")
                .build();

        List<BookingResponseDto> ownerBookings = List.of(ownerBooking1, ownerBooking2);

        when(bookingService.getBookingsForOwner(eq(userId), eq(state), eq(page), eq(size)))
                .thenReturn(List.of(new Booking(), new Booking()));
        when(bookingService.getListBookingResponseDto(any(List.class)))
                .thenReturn(ownerBookings);

        // When & Then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(3L))
                .andExpect(jsonPath("$[0].item.name").value("Tablet"))
                .andExpect(jsonPath("$[0].booker.id").value(3L))
                .andExpect(jsonPath("$[0].booker.name").value("Sergey Sergeev"))
                .andExpect(jsonPath("$[1].id").value(4L))
                .andExpect(jsonPath("$[1].status").value("WAITING"))
                .andExpect(jsonPath("$[1].item.id").value(4L))
                .andExpect(jsonPath("$[1].item.name").value("Camera"))
                .andExpect(jsonPath("$[1].booker.id").value(4L))
                .andExpect(jsonPath("$[1].booker.name").value("Anna Ananova"))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[1].start").exists())
                .andExpect(jsonPath("$[1].end").exists());

        verify(bookingService, times(1)).getBookingsForOwner(eq(userId), eq(state), eq(page), eq(size));
    }
}