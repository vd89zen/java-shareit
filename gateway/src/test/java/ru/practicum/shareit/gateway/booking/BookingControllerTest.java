package ru.practicum.shareit.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.booking.dto.BookingState;

import java.time.LocalDateTime;

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
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание бронирования — успешный ответ")
    void createBooking_Should_Return_Created_Test() throws Exception {
        // Given
        long userId = 1L;
        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setStart(LocalDateTime.now().plusDays(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        newBookingDto.setItemId(1L);

        when(bookingClient.createBooking(eq(userId), any(NewBookingDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).createBooking(eq(userId), any(NewBookingDto.class));
    }

    @Test
    @DisplayName("Подтверждение/отклонение бронирования — успешный ответ")
    void approveOrRejectBooking_Should_Return_Updated_Booking_Test() throws Exception {
        // Given
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        when(bookingClient.approveOrRejectBooking(eq(userId), eq(bookingId), eq(approved)))
                .thenReturn(ResponseEntity.ok().body("Updated booking"));

        // When & Then
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated booking"));

        verify(bookingClient, times(1)).approveOrRejectBooking(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    @DisplayName("Получение бронирования по ID — успешный ответ")
    void getBooking_Should_Return_Booking_Test() throws Exception {
        // Given
        long userId = 1L;
        long bookingId = 1L;

        when(bookingClient.getBooking(eq(userId), eq(bookingId)))
                .thenReturn(ResponseEntity.ok().body("Booking data"));

        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking data"));

        verify(bookingClient, times(1)).getBooking(eq(userId), eq(bookingId));
    }

    @Test
    @DisplayName("Получение бронирований для букера — успешный ответ с пагинацией")
    void getBookingsForBooker_Should_Return_All_Bookings_With_Pagination_Test() throws Exception {
        // Given
        long userId = 1L;
        String stateParam = "all";
        int page = 0;
        int size = 10;

        when(bookingClient.getBookingsForBooker(eq(userId), eq(BookingState.ALL), eq(page), eq(size)))
                .thenReturn(ResponseEntity.ok().body("[\"Booking1\", \"Booking2\"]"));

        // When & Then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateParam)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Booking1\", \"Booking2\"]"));

        verify(bookingClient, times(1)).getBookingsForBooker(eq(userId), eq(BookingState.ALL), eq(page), eq(size));
    }

    @Test
    @DisplayName("Получение бронирований для владельца — успешный ответ с пагинацией")
    void getBookingsForOwner_Should_Return_All_Bookings_For_Owner_With_Pagination_Test() throws Exception {
        // Given
        long userId = 1L;
        String stateParam = "waiting";
        int page = 0;
        int size = 10;

        when(bookingClient.getBookingsForOwner(eq(userId), eq(BookingState.WAITING), eq(page), eq(size)))
                .thenReturn(ResponseEntity.ok().body("[\"OwnerBooking1\", \"OwnerBooking2\"]"));

        // When & Then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateParam)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"OwnerBooking1\", \"OwnerBooking2\"]"));

        verify(bookingClient, times(1)).getBookingsForOwner(eq(userId), eq(BookingState.WAITING), eq(page), eq(size));
    }

}
