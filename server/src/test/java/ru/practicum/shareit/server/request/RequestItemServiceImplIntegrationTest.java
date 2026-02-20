package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemShortDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.server.request.model.RequestItem;
import ru.practicum.shareit.server.user.dto.NewUserDto;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Интеграционные тесты для метода getAllRequestItemByRequestor сервиса RequestItemServiceImpl")
class RequestItemServiceImplIntegrationTest {

    @Autowired
    private RequestItemService requestItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestItemRepository requestItemRepository;

    @Test
    @DisplayName("Возвращает запросы с вещами, если есть запросы")
    void getAllRequestItemByRequestor_Should_Return_Requests_With_Items_When_User_Has_Requests_Test() {
        // Given
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Test User");
        newUserDto.setEmail("test@user.com");
        User requestor = userService.createUser(newUserDto);

        RequestItem request = RequestItem.builder()
                .description("Need tools")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        request = requestItemRepository.save(request);

        Item item = Item.builder()
                .name("Hammer")
                .description("Good hammer")
                .available(true)
                .owner(requestor)
                .request(request)
                .build();
        itemRepository.save(item);

        int page = 0;
        int size = 10;

        // When
        List<RequestItemResponseDto> result = requestItemService.getAllRequestItemByRequestor(requestor.getId(), page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        RequestItemResponseDto responseDto = result.get(0);
        assertEquals("Need tools", responseDto.getDescription());
        assertEquals(requestor.getId(), responseDto.getRequestorId());

        List<ItemShortDto> items = responseDto.getItems();
        assertNotNull(items);
        assertEquals(1, items.size());

        ItemShortDto itemDto = items.get(0);
        assertEquals("Hammer", itemDto.getName());
        assertEquals(requestor.getId(), itemDto.getOwnerId());
    }

    @Test
    @DisplayName("Возвращает пустой список, если нет запросов")
    void getAllRequestItemByRequestor_Should_Return_Empty_List_When_User_Has_No_Requests_Test() {
        // Given
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("No Requests User");
        newUserDto.setEmail("no-requests@test.com");
        User user = userService.createUser(newUserDto);

        int page = 0;
        int size = 10;

        // When
        List<RequestItemResponseDto> result = requestItemService.getAllRequestItemByRequestor(user.getId(), page, size);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Работает с пагинацией при множестве запросов")
    void getAllRequestItemByRequestor_Should_Handle_Pagination_When_Many_Requests_Exist_Test() {
        // Given
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Paginated User");
        newUserDto.setEmail("paginated@test.com");
        User requestor = userService.createUser(newUserDto);

        for (int i = 0; i < 15; i++) {
            RequestItem request = RequestItem.builder()
                    .description("Request " + i)
                    .requestor(requestor)
                    .created(LocalDateTime.now())
                    .build();
            requestItemRepository.save(request);
        }

        int pageSize = 10;

        // When
        List<RequestItemResponseDto> firstPage = requestItemService.getAllRequestItemByRequestor(requestor.getId(), 0, pageSize);
        List<RequestItemResponseDto> secondPage = requestItemService.getAllRequestItemByRequestor(requestor.getId(), 1, pageSize);

        // Then
        assertEquals(pageSize, firstPage.size()); // 10 на первой странице
        assertEquals(5, secondPage.size());  // 5 на второй странице
    }
}
