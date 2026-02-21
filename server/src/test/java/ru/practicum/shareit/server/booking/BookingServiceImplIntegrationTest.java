package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingState;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.ItemService;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.UserService;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Интеграционные тесты для метода getBookingsForOwner сервиса BookingServiceImpl")
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    private static final int PAGE = 0;
    private static final int SIZE = 10;

    private User createUser(String name, String email) {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName(name);
        newUserDto.setEmail(email);
        return userService.createUser(newUserDto);
    }

    private Item createItem(User owner, String name, String description) {
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName(name);
        newItemDto.setDescription(description);
        newItemDto.setAvailable(true);
        return itemService.createItem(owner.getId(), newItemDto);
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end, Status status) {
        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(item.getId());
        newBookingDto.setStart(start);
        newBookingDto.setEnd(end);

        Booking booking = BookingMapper.toBooking(newBookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Test
    @DisplayName("Возвращает все бронирования владельца")
    void getBookingsForOwner_Should_Return_All_Bookings_For_Owner_Test() {
        // Given
        User owner = createUser("Owner User", "owner@test.com");
        User booker = createUser("Booker User", "booker@test.com");
        Item item = createItem(owner, "Test Item", "Test description");

        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.APPROVED);
        Booking booking2 = createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.WAITING);

        // When
        List<Booking> result = bookingService.getBookingsForOwner(owner.getId(), BookingState.ALL, PAGE, SIZE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(booking1));
        assertTrue(result.contains(booking2));
    }

    @Test
    @DisplayName("Возвращает пустые бронирования, если у владельца нет вещей")
    void getBookingsForOwner_Should_Return_Empty_List_When_Owner_Has_No_Items_Test() {
        // Given
        User owner = createUser("No Items Owner", "no-items@test.com");

        // When
        List<Booking> result = bookingService.getBookingsForOwner(owner.getId(), BookingState.ALL, PAGE, SIZE);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Фильтрует бронирования по статусу WAITING")
    void getBookingsForOwner_Should_Filter_By_Waiting_Status_Test() {
        // Given
        User owner = createUser("Filtering Owner", "filtering@test.com");
        User booker = createUser("Filter Booker", "filter-booker@test.com");
        Item item = createItem(owner, "Filter Item", "Filter description");

        LocalDateTime now = LocalDateTime.now();

        Booking waitingBooking = createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.WAITING);
        Booking approvedBooking = createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.APPROVED);

        // When
        List<Booking> result = bookingService.getBookingsForOwner(owner.getId(), BookingState.WAITING, PAGE, SIZE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Status.WAITING, result.get(0).getStatus());
        assertFalse(result.contains(approvedBooking));
    }

    @Test
    @DisplayName("Работает с пагинацией при множестве бронирований")
    void getBookingsForOwner_Should_Handle_Pagination_When_Many_Bookings_Exist_Test() {
        // Given
        User owner = createUser("Paginated Owner", "paginated@test.com");
        User booker = createUser("Pagination Booker", "pagination-booker@test.com");
        Item item = createItem(owner, "Pagination Item", "Pagination description");

        LocalDateTime startDate = LocalDateTime.now();

        for (int i = 0; i < 15; i++) {
            createBooking(booker, item, startDate.plusDays(i * 2), startDate.plusDays(i * 2 + 1), Status.APPROVED);
        }

        int pageSize = 10;

        // When
        List<Booking> firstPage = bookingService.getBookingsForOwner(owner.getId(), BookingState.ALL, 0, pageSize);
        List<Booking> secondPage = bookingService.getBookingsForOwner(owner.getId(), BookingState.ALL, 1, pageSize);

        // Then
        assertEquals(pageSize, firstPage.size());
        assertEquals(5, secondPage.size());
    }

    @Test
    @DisplayName("Возвращает бронирования за прошедший период")
    void getBookingsForOwner_Should_Return_Past_Bookings_Test() {
        // Given
        User owner = createUser("Past Owner", "past@test.com");
        User booker = createUser("Past Booker", "past-booker@test.com");
        Item item = createItem(owner, "Past Item", "Past description");

        LocalDateTime now = LocalDateTime.now();

        Booking pastBooking = createBooking(
                booker,
                item,
                now.minusDays(5),
                now.minusDays(3),
                Status.APPROVED
        );

        createBooking(
                booker,
                item,
                now.plusDays(1),
                now.plusDays(2),
                Status.APPROVED
        );

        // When
        List<Booking> result = bookingService.getBookingsForOwner(owner.getId(), BookingState.PAST, PAGE, SIZE);

        // Then
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1, result.size(), "Должно вернуться ровно одно прошлое бронирование");
        assertEquals(
                pastBooking.getId(),
                result.get(0).getId(),
                "ID бронирования в результате должен совпадать с ID прошлого бронирования"
        );
        assertFalse(
                result.stream().anyMatch(b -> b.getStart().isAfter(now)),
                "Результат не должен содержать бронирований с началом в будущем"
        );
    }

    @Test
    @DisplayName("Возвращает текущие бронирования")
    void getBookingsForOwner_Should_Return_Current_Bookings_Test() {
        // Given
        User owner = createUser("Current Owner", "current@test.com");
        User booker = createUser("Current Booker", "current-booker@test.com");
        Item item = createItem(owner, "Current Item", "Current description");

        LocalDateTime now = LocalDateTime.now();

        Booking currentBooking = createBooking(
                booker,
                item,
                now.minusHours(1),
                now.plusHours(1),
                Status.APPROVED
        );

        createBooking(
                booker,
                item,
                now.minusDays(2),
                now.minusDays(1),
                Status.APPROVED
        );

        // When
        List<Booking> result = bookingService.getBookingsForOwner(owner.getId(), BookingState.CURRENT, PAGE, SIZE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
        assertFalse(result.contains(createBooking(
                booker,
                item,
                now.minusDays(2),
                now.minusDays(1),
                Status.APPROVED
        )));
    }

    @Test
    @DisplayName("Выбрасывает исключение при несуществующем владельце")
    void getBookingsForOwner_Should_Throw_Exception_When_Owner_Not_Exists_Test() {
        // Given
        Long nonExistentOwnerId = 999L;

        // When & Then
        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingsForOwner(nonExistentOwnerId, BookingState.ALL, PAGE, SIZE)
        );
    }

    @Test
    @DisplayName("Фильтрует бронирования по статусу REJECTED")
    void getBookingsForOwner_Should_Filter_By_Rejected_Status_Test() {
        // Given
        User owner = createUser("Rejected Owner", "rejected@test.com");
        User booker = createUser("Rejected Booker", "rejected-booker@test.com");
        Item item = createItem(owner, "Rejected Item", "Rejected description");

        LocalDateTime now = LocalDateTime.now();

        Booking rejectedBooking = createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.REJECTED);
        createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.APPROVED);

        // When
        List<Booking> result = bookingService.getBookingsForOwner(owner.getId(), BookingState.REJECTED, PAGE, SIZE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Status.REJECTED, result.get(0).getStatus());
        assertFalse(result.contains(createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.APPROVED)));
    }

    @Test
    @DisplayName("Возвращает бронирования на будущее")
    void getBookingsForOwner_Should_Return_Future_Bookings_Test() {
        // Given
        User owner = createUser("Future Owner", "future@test.com");
        User booker = createUser("Future Booker", "future-booker@test.com");
        Item item = createItem(owner, "Future Item", "Future description");

        LocalDateTime now = LocalDateTime.now();

        Booking futureBooking = createBooking(booker, item, now.plusDays(5), now.plusDays(7), Status.APPROVED);
        createBooking(booker, item, now.minusDays(2), now.minusDays(1), Status.APPROVED);

        // When
        List<Booking> result = bookingService.getBookingsForOwner(owner.getId(), BookingState.FUTURE, PAGE, SIZE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
        assertFalse(result.contains(createBooking(booker, item, now.minusDays(2), now.minusDays(1), Status.APPROVED)));
    }

    @Test
    @DisplayName("Корректно обрабатывает случай, когда у владельца несколько вещей")
    void getBookingsForOwner_Should_Handle_Multiple_Items_Test() {
        // Given
        User owner = createUser("Multiple Items Owner", "multiple@test.com");
        User booker = createUser("Multiple Booker", "multiple-booker@test.com");

        Item item1 = createItem(owner, "Item 1", "First item");
        Item item2 = createItem(owner, "Item 2", "Second item");

        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = createBooking(booker, item1, now.plusDays(1), now.plusDays(2), Status.APPROVED);
        Booking booking2 = createBooking(booker, item2, now.plusDays(3), now.plusDays(4), Status.WAITING);

        // When
        List<Booking> result = bookingService.getBookingsForOwner(owner.getId(), BookingState.ALL, PAGE, SIZE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(booking1));
        assertTrue(result.contains(booking2));
    }
}