package ru.practicum.shareit.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.item.dto.*;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@DisplayName("Тесты для ItemController")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 1L;

    private ItemResponseDto createItemResponseDto(long id, String name, String description, boolean available) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private ItemResponseForOwnerDto createItemResponseForOwnerDto(long id, String name, String description, boolean available) {
        ItemResponseForOwnerDto dto = new ItemResponseForOwnerDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private CommentResponseDto createCommentResponseDto(long id, String text, String authorName, LocalDateTime created) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(id);
        dto.setText(text);
        dto.setAuthorName(authorName);
        dto.setCreated(created);
        return dto;
    }

    @Test
    @DisplayName("Создание вещи — успешный ответ")
    void createItem_Should_Return_Created_Test() throws Exception {
        // Given
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Test Item");
        newItemDto.setDescription("Test description");
        newItemDto.setAvailable(true);

        ItemResponseDto itemResponseDto = createItemResponseDto(
                1L,
                "Test Item",
                "Test description",
                true
        );


        when(itemService.createItem(eq(USER_ID), any(NewItemDto.class)))
                .thenReturn(new Item());
        when(itemService.getItemResponseDto(any(Item.class)))
                .thenReturn(itemResponseDto);

        // When & Then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).createItem(eq(USER_ID), any(NewItemDto.class));
    }

    @Test
    @DisplayName("Получение вещи по ID — успешный ответ")
    void getItemById_Should_Return_Item_Test() throws Exception {
        // Given
        ItemResponseDto itemResponseDto = createItemResponseDto(
                ITEM_ID,
                "Existing Item",
                "Existing description",
                true
        );


        when(itemService.getItemById(eq(ITEM_ID)))
                .thenReturn(new Item());
        when(itemService.getItemResponseDto(any(Item.class)))
                .thenReturn(itemResponseDto);

        // When & Then
        mockMvc.perform(get("/items/{itemId}", ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value("Existing Item"))
                .andExpect(jsonPath("$.description").value("Existing description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).getItemById(eq(ITEM_ID));
    }

    @Test
    @DisplayName("Получение всех вещей владельца — успешный ответ")
    void getAllItemForOwner_Should_Return_All_Items_For_Owner_Test() throws Exception {
        // Given
        ItemResponseForOwnerDto item1 = createItemResponseForOwnerDto(
                1L,
                "Owner Item 1",
                "Owner description 1",
                true
        );

        ItemResponseForOwnerDto item2 = createItemResponseForOwnerDto(
                2L,
                "Owner Item 2",
                "Owner description 2",
                false
        );

        List<ItemResponseForOwnerDto> items = List.of(item1, item2);

        when(itemService.getAllItemForOwner(eq(USER_ID)))
                .thenReturn(List.of(new Item(), new Item()));
        when(itemService.getListItemResponseForOwnerDto(any(List.class)))
                .thenReturn(items);

        // When & Then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Owner Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Owner Item 2"));

        verify(itemService, times(1)).getAllItemForOwner(eq(USER_ID));
    }

    @Test
    @DisplayName("Обновление вещи по ID — успешный ответ")
    void updateItemById_Should_Return_Updated_Item_Test() throws Exception {
        // Given
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Name");
        itemUpdateDto.setDescription("Updated description");
        itemUpdateDto.setAvailable(false);

        ItemResponseDto updatedItemResponseDto = createItemResponseDto(
                ITEM_ID,
                "Updated Name",
                "Updated description",
                false
        );


        when(itemService.updateItemById(eq(USER_ID), eq(ITEM_ID), any(ItemUpdateDto.class)))
                .thenReturn(new Item());
        when(itemService.getItemResponseDto(any(Item.class)))
                .thenReturn(updatedItemResponseDto);

        // When & Then
        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService, times(1)).updateItemById(eq(USER_ID), eq(ITEM_ID), any(ItemUpdateDto.class));
    }

    @Test
    @DisplayName("Удаление вещи по ID — успешный ответ (No Content)")
    void deleteItemById_Should_Return_No_Content_Test() throws Exception {
        // Given
        doNothing().when(itemService).deleteItemById(eq(USER_ID), eq(ITEM_ID));

        // When & Then
        mockMvc.perform(delete("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItemById(eq(USER_ID), eq(ITEM_ID));
    }

    @Test
    @DisplayName("Поиск вещей по тексту — успешный ответ")
    void searchItem_Should_Return_Items_By_Text_Test() throws Exception {
        // Given
        String text = "test";

        ItemResponseDto item1 = createItemResponseDto(
                1L,
                "Search Item 1",
                "Contains test keyword",
                true
        );

        ItemResponseDto item2 = createItemResponseDto(
                2L,
                "Another Test Item",
                "Another description with test",
                false
        );


        List<ItemResponseDto> items = List.of(item1, item2);

        when(itemService.searchItem(eq(text)))
                .thenReturn(List.of(new Item(), new Item()));
        when(itemService.getListItemResponseDto(any(List.class)))
                .thenReturn(items);

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Search Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Another Test Item"));

        verify(itemService, times(1)).searchItem(eq(text));
    }

    @Test
    @DisplayName("Добавление комментария к вещи — успешный ответ (CREATED)")
    void commentItemById_Should_Return_Created_Comment_Test() throws Exception {
        // Given
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("Great item!");

        CommentResponseDto commentResponseDto = createCommentResponseDto(
                1L,
                "Great item!",
                "Test User",
                LocalDateTime.now()
        );


        when(itemService.commentItemById(eq(USER_ID), eq(ITEM_ID), any(NewCommentDto.class)))
                .thenReturn(new Comment());
        when(itemService.getCommentResponseDto(any(Comment.class)))
                .thenReturn(commentResponseDto);

        // When & Then
        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("Test User"))
                .andExpect(jsonPath("$.created").exists());

        verify(itemService, times(1)).commentItemById(eq(USER_ID), eq(ITEM_ID), any(NewCommentDto.class));
    }
}