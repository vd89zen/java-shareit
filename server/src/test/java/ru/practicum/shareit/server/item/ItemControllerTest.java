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

    @Test
    @DisplayName("Создание вещи — успешный ответ")
    void createItem_Should_Return_Created_Test() throws Exception {
        // Given
        long userId = 1L;
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Test Item");
        newItemDto.setDescription("Test description");
        newItemDto.setAvailable(true);

        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setName("Test Item");
        itemResponseDto.setDescription("Test description");
        itemResponseDto.setAvailable(true);

        when(itemService.createItem(eq(userId), any(NewItemDto.class)))
                .thenReturn(new Item());
        when(itemService.getItemResponseDto(any(Item.class)))
                .thenReturn(itemResponseDto);

        // When & Then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).createItem(eq(userId), any(NewItemDto.class));
    }

    @Test
    @DisplayName("Получение вещи по ID — успешный ответ")
    void getItemById_Should_Return_Item_Test() throws Exception {
        // Given
        long itemId = 1L;

        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(itemId);
        itemResponseDto.setName("Existing Item");
        itemResponseDto.setDescription("Existing description");
        itemResponseDto.setAvailable(true);

        when(itemService.getItemById(eq(itemId)))
                .thenReturn(new Item());
        when(itemService.getItemResponseDto(any(Item.class)))
                .thenReturn(itemResponseDto);

        // When & Then
        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Existing Item"))
                .andExpect(jsonPath("$.description").value("Existing description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).getItemById(eq(itemId));
    }

    @Test
    @DisplayName("Получение всех вещей владельца — успешный ответ")
    void getAllItemForOwner_Should_Return_All_Items_For_Owner_Test() throws Exception {
        // Given
        long userId = 1L;

        ItemResponseForOwnerDto item1 = new ItemResponseForOwnerDto();
        item1.setId(1L);
        item1.setName("Owner Item 1");
        item1.setDescription("Owner description 1");
        item1.setAvailable(true);

        ItemResponseForOwnerDto item2 = new ItemResponseForOwnerDto();
        item2.setId(2L);
        item2.setName("Owner Item 2");
        item2.setDescription("Owner description 2");
        item2.setAvailable(false);

        List<ItemResponseForOwnerDto> items = List.of(item1, item2);

        when(itemService.getAllItemForOwner(eq(userId)))
                .thenReturn(List.of(new Item(), new Item()));
        when(itemService.getListItemResponseForOwnerDto(any(List.class)))
                .thenReturn(items);

        // When & Then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Owner Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Owner Item 2"));

        verify(itemService, times(1)).getAllItemForOwner(eq(userId));
    }

    @Test
    @DisplayName("Обновление вещи по ID — успешный ответ")
    void updateItemById_Should_Return_Updated_Item_Test() throws Exception {
        // Given
        long userId = 1L;
        long itemId = 1L;
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Name");
        itemUpdateDto.setDescription("Updated description");
        itemUpdateDto.setAvailable(false);

        ItemResponseDto updatedItemResponseDto = new ItemResponseDto();
        updatedItemResponseDto.setId(itemId);
        updatedItemResponseDto.setName("Updated Name");
        updatedItemResponseDto.setDescription("Updated description");
        updatedItemResponseDto.setAvailable(false);

        when(itemService.updateItemById(eq(userId), eq(itemId), any(ItemUpdateDto.class)))
                .thenReturn(new Item());
        when(itemService.getItemResponseDto(any(Item.class)))
                .thenReturn(updatedItemResponseDto);

        // When & Then
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService, times(1)).updateItemById(eq(userId), eq(itemId), any(ItemUpdateDto.class));
    }

    @Test
    @DisplayName("Удаление вещи по ID — успешный ответ (No Content)")
    void deleteItemById_Should_Return_No_Content_Test() throws Exception {
        // Given
        long userId = 1L;
        long itemId = 1L;

        doNothing().when(itemService).deleteItemById(eq(userId), eq(itemId));

        // When & Then
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItemById(eq(userId), eq(itemId));
    }

    @Test
    @DisplayName("Поиск вещей по тексту — успешный ответ")
    void searchItem_Should_Return_Items_By_Text_Test() throws Exception {
        // Given
        String text = "test";

        ItemResponseDto item1 = new ItemResponseDto();
        item1.setId(1L);
        item1.setName("Search Item 1");
        item1.setDescription("Contains test keyword");
        item1.setAvailable(true);

        ItemResponseDto item2 = new ItemResponseDto();
        item2.setId(2L);
        item2.setName("Another Test Item");
        item2.setDescription("Another description with test");
        item2.setAvailable(false);

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
        long userId = 1L;
        long itemId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("Great item!");

        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(1L);
        commentResponseDto.setText("Great item!");
        commentResponseDto.setAuthorName("Test User");
        commentResponseDto.setCreated(java.time.LocalDateTime.now());

        when(itemService.commentItemById(eq(userId), eq(itemId), any(NewCommentDto.class)))
                .thenReturn(new Comment());
        when(itemService.getCommentResponseDto(any(Comment.class)))
                .thenReturn(commentResponseDto);

        // When & Then
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("Test User"))
                .andExpect(jsonPath("$.created").exists());

        verify(itemService, times(1)).commentItemById(eq(userId), eq(itemId), any(NewCommentDto.class));
    }
}