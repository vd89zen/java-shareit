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

    private static final long USER_ID = 1L;
    private static final long BOOKING_ID = 1L;
    private static final int PAGE = 0;
    private static final int SIZE = 10;

    private NewBookingDto createNewBookingDto() {
        NewBookingDto dto = new NewBookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setItemId(1L);
        return dto;
    }

    private ItemResponseDto createItemResponseDto(long id, String name, String description, boolean available) {
        return ItemResponseDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .build();
    }

    private UserResponseDto createUserResponseDto(long id, String name, String email) {
        return UserResponseDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    private BookingResponseDto createBookingResponseDto(
            long id, LocalDateTime start, LocalDateTime end,
            ItemResponseDto item, UserResponseDto booker, String status) {
        return BookingResponseDto.builder()
                .id(id)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }

    private List<Booking> createMockBookingList() {
        return List.of(new Booking(), new Booking());
    }

    @Test
    @DisplayName("Создание брони — успешный ответ")
    void createBooking_Should_Return_Created_Test() throws Exception {
        // Given
        NewBookingDto newBookingDto = createNewBookingDto();

        ItemResponseDto itemResponseDto = createItemResponseDto(
                1L, "Test Item", "Test description", true);

        UserResponseDto bookerResponseDto = createUserResponseDto(
                USER_ID, "Test User", "user@test.com");

        BookingResponseDto bookingResponseDto = createBookingResponseDto(
                BOOKING_ID, newBookingDto.getStart(), newBookingDto.getEnd(),
                itemResponseDto, bookerResponseDto, "WAITING");

        when(bookingService.createBooking(eq(USER_ID), any(NewBookingDto.class)))
                .thenReturn(new Booking());
        when(bookingService.getBookingResponseDto(any(Booking.class)))
                .thenReturn(bookingResponseDto);

        // When & Then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Test Item"))
                .andExpect(jsonPath("$.booker.id").value(USER_ID))
                .andExpect(jsonPath("$.booker.name").value("Test User"));

        verify(bookingService, times(1)).createBooking(eq(USER_ID), any(NewBookingDto.class));
    }

    @Test
    @DisplayName("Подтверждение/отказ брони — успешный ответ")
    void approveOrRejectBookingById_Should_Return_Updated_Booking_Test() throws Exception {
        // Given
        boolean approved = true;

        ItemResponseDto itemResponseDto = createItemResponseDto(
                1L, "Booked Item", "Item for booking", false);

        UserResponseDto bookerResponseDto = createUserResponseDto(
                2L, "Booker User", "booker@test.com");

        BookingResponseDto updatedBookingResponseDto = createBookingResponseDto(
                BOOKING_ID, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                itemResponseDto, bookerResponseDto, approved ? "APPROVED" : "REJECTED");

        when(bookingService.approveOrRejectBookingById(eq(USER_ID), eq(BOOKING_ID), eq(approved)))
                .thenReturn(new Booking());
        when(bookingService.getBookingResponseDto(any(Booking.class)))
                .thenReturn(updatedBookingResponseDto);

        // When & Then
        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(2L));

        verify(bookingService, times(1)).approveOrRejectBookingById(eq(USER_ID), eq(BOOKING_ID), eq(approved));
    }

    @Test
    @DisplayName("Получение брони по ID с проверкой доступа — успешный ответ")
    void getBookingByIdWithAccessCheck_Should_Return_Booking_Test() throws Exception {
        // Given
        ItemResponseDto itemResponseDto = createItemResponseDto(
                1L, "Access Item", "Item with access", true);

        UserResponseDto bookerResponseDto = createUserResponseDto(
                3L, "Access User", "access@test.com");

        BookingResponseDto bookingResponseDto = createBookingResponseDto(
                BOOKING_ID, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                itemResponseDto, bookerResponseDto, "WAITING");

        when(bookingService.getBookingByIdWithAccessCheck(eq(USER_ID), eq(BOOKING_ID)))
                .thenReturn(new Booking());
        when(bookingService.getBookingResponseDto(any(Booking.class)))
                .thenReturn(bookingResponseDto);

        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(3L))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists());

        verify(bookingService, times(1)).getBookingByIdWithAccessCheck(eq(USER_ID), eq(BOOKING_ID));
    }


    @Test
    @DisplayName("Получение всех броней для арендатора — успешный ответ с пагинацией")
    void getBookingsForBooker_Should_Return_All_Bookings_With_Pagination_Test() throws Exception {
        // Given
        BookingState state = BookingState.ALL;

        ItemResponseDto item1 = createItemResponseDto(
                1L, "Laptop", "Gaming laptop", true);
        UserResponseDto booker1 = createUserResponseDto(
                USER_ID, "Ivan Ivanov", "ivan@test.com");
        BookingResponseDto booking1 = createBookingResponseDto(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item1, booker1, "APPROVED");


        ItemResponseDto item2 = createItemResponseDto(
                2L, "Phone", "Smartphone", false);
        UserResponseDto booker2 = createUserResponseDto(
                2L, "Petr Petrov", "petr@test.com");
        BookingResponseDto booking2 = createBookingResponseDto(
                2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
                item2, booker2, "WAITING");

        List<BookingResponseDto> bookings = List.of(booking1, booking2);

        when(bookingService.getBookingsForBooker(eq(USER_ID), eq(state), eq(PAGE), eq(SIZE)))
                .thenReturn(createMockBookingList());
        when(bookingService.getListBookingResponseDto(any(List.class)))
                .thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", state.name())
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Laptop"))
                .andExpect(jsonPath("$[0].booker.id").value(USER_ID))
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

        verify(bookingService, times(1)).getBookingsForBooker(eq(USER_ID), eq(state), eq(PAGE), eq(SIZE));
    }

    @Test
    @DisplayName("Получение всех броней для владельца — успешный ответ с пагинацией")
    void getBookingsForOwner_Should_Return_All_Bookings_For_Owner_With_Pagination_Test() throws Exception {
        // Given
        BookingState state = BookingState.CURRENT;

        ItemResponseDto ownerItem1 = createItemResponseDto(
                3L, "Tablet", "Digital tablet", true);
        UserResponseDto ownerBooker1 = createUserResponseDto(
                3L, "Sergey Sergeev", "sergey@test.com");
        BookingResponseDto ownerBooking1 = createBookingResponseDto(
                3L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                ownerItem1, ownerBooker1, "APPROVED");

        ItemResponseDto ownerItem2 = createItemResponseDto(
                4L, "Camera", "DSLR camera", false);
        UserResponseDto ownerBooker2 = createUserResponseDto(
                4L, "Anna Ananova", "anna@test.com");
        BookingResponseDto ownerBooking2 = createBookingResponseDto(
                4L, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(7),
                ownerItem2, ownerBooker2, "WAITING");

        List<BookingResponseDto> ownerBookings = List.of(ownerBooking1, ownerBooking2);

        when(bookingService.getBookingsForOwner(eq(USER_ID), eq(state), eq(PAGE), eq(SIZE)))
                .thenReturn(createMockBookingList());
        when(bookingService.getListBookingResponseDto(any(List.class)))
                .thenReturn(ownerBookings);

        // When & Then
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", state.name())
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
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

        verify(bookingService, times(1)).getBookingsForOwner(eq(USER_ID), eq(state), eq(PAGE), eq(SIZE));
    }
}