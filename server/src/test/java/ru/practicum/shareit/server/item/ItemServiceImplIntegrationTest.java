package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.dto.NewBookingDto;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.exception.WrongRequestException;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.item.dto.NewCommentDto;
import ru.practicum.shareit.server.item.model.Comment;
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
@DisplayName("Интеграционные тесты для методов searchItem, commentItemById сервиса ItemServiceImpl")
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User user;

    @BeforeEach
    void setUp() {
        owner = userService.createUser(new NewUserDto("Owner", "owner@test.com"));
        user = userService.createUser(new NewUserDto("User", "user@test.com"));
    }

    private Item createItem(User owner, String name, String description, boolean available) {
        return itemService.createItem(
                owner.getId(),
                new NewItemDto(name, description, available, null));
    }

    private Booking createCompletedBooking(User booker, Item item) {
        NewBookingDto newBookingDto = new NewBookingDto(
                item.getId(),
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(2)
        );

        Booking newBooking = BookingMapper.toBooking(newBookingDto);
        newBooking.setBooker(booker);
        newBooking.setItem(item);
        newBooking.setStatus(Status.WAITING);
        newBooking = bookingRepository.save(newBooking);
        newBooking.setStatus(Status.APPROVED);
        newBooking.setItem(itemService.updateItemAvailable(newBooking.getItem(), false));
        return bookingRepository.save(newBooking);
    }

    private NewCommentDto createNewCommentDto(String text) {
        return new NewCommentDto(text);
    }

    @Test
    @DisplayName("Поиск вещей по тексту (есть совпадения)")
    void searchItem_Should_Find_Items_By_Name_Or_Description_Test() {
        // Given
        createItem(owner, "Молоток", "Хороший молоток для ремонта", true);
        createItem(owner, "Отвёртка", "Набор отвёрток разных размеров", true);

        // When
        List<Item> result = itemService.searchItem("молот");

        // Then
        assertEquals(1, result.size());
        assertEquals("Молоток", result.get(0).getName());
    }

    @Test
    @DisplayName("Поиск по пустому тексту возвращает пустой список")
    void searchItem_Should_Return_Empty_List_When_Text_Is_Empty_Test() {
        // Given, When
        List<Item> result = itemService.searchItem("");
        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Поиск без совпадений возвращает пустой список")
    void searchItem_Should_Return_Empty_List_When_No_Matches_Test() {
        // Given
        createItem(owner, "Стул", "Деревянный стул", true);

        // When
        List<Item> result = itemService.searchItem("стол");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Поиск с учётом регистра (игнорирует регистр)")
    void searchItem_Should_Ignore_Case_Test() {
        // Given
        createItem(owner, "Ноутбук", "Мощный игровой ноутбук", true);

        // When
        List<Item> result = itemService.searchItem("ноутбук");

        // Then
        assertEquals(1, result.size());
        assertEquals("Ноутбук", result.get(0).getName());
    }

    @Test
    @DisplayName("Добавление комментария при наличии завершённого бронирования")
    void commentItemById_Should_Add_Comment_When_User_Has_Completed_Booking_Test() {
        // Given
        Item item = createItem(owner, "Вещь для бронирования", "Вещь для бронирования", true);
        createCompletedBooking(user, item);

        // When
        NewCommentDto commentDto = createNewCommentDto("Отличный товар, очень доволен!");
        Comment comment = itemService.commentItemById(user.getId(), item.getId(), commentDto);

        // Then
        assertNotNull(comment);
        assertEquals("Отличный товар, очень доволен!", comment.getText());
        assertEquals(user, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertNotNull(comment.getCreated());
    }

    @Test
    @DisplayName("Ошибка при попытке комментария без бронирования")
    void commentItemById_Should_Throw_Exception_When_No_Booking_Test() {
        // Given
        Item item = createItem(owner, "Вещь без бронирования", "Вещь без бронирования", true);

        // Then
        assertThrows(WrongRequestException.class, () -> {
            itemService.commentItemById(user.getId(), item.getId(),
                    createNewCommentDto("Мой комментарий"));
        });
    }
}
