package ru.practicum.shareit.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;
import ru.practicum.shareit.gateway.item.dto.NewCommentDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@DisplayName("Тесты для ItemController")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

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
        newItemDto.setRequestId(null);

        when(itemClient.createItem(eq(userId), any(NewItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).createItem(eq(userId), any(NewItemDto.class));
    }

    @Test
    @DisplayName("Получение вещи по ID — успешный ответ")
    void getItemById_Should_Return_Item_Test() throws Exception {
        // Given
        Long itemId = 1L;
        when(itemClient.getItemById(itemId))
                .thenReturn(ResponseEntity.ok().body("Item data"));

        // When & Then
        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().string("Item data"));

        verify(itemClient, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("Получение всех вещей владельца — успешный ответ с пагинацией")
    void getAllItemForOwner_Should_Return_All_Items_With_Pagination_Test() throws Exception {
        // Given
        long userId = 1L;
        int page = 0;
        int size = 10;

        when(itemClient.getAllItemForOwner(eq(userId), eq(page), eq(size)))
                .thenReturn(ResponseEntity.ok().body("[\"Item1\", \"Item2\"]"));

        // When & Then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Item1\", \"Item2\"]"));

        verify(itemClient, times(1)).getAllItemForOwner(eq(userId), eq(page), eq(size));
    }

    @Test
    @DisplayName("Обновление вещи по ID — успешный ответ")
    void updateItemById_Should_Return_Updated_Item_Test() throws Exception {
        // Given
        long userId = 1L;
        long itemId = 1L;
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Name");

        when(itemClient.updateItemById(eq(userId), eq(itemId), any(ItemUpdateDto.class)))
                .thenReturn(ResponseEntity.ok().body("Updated item"));

        // When & Then
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated item"));

        verify(itemClient, times(1)).updateItemById(eq(userId), eq(itemId), any(ItemUpdateDto.class));
    }

    @Test
    @DisplayName("Удаление вещи по ID — успешный ответ (No Content)")
    void deleteItemById_Should_Return_No_Content_Test() throws Exception {
        // Given
        long userId = 1L;
        long itemId = 1L;
        when(itemClient.deleteItemById(eq(userId), eq(itemId)))
                .thenReturn(ResponseEntity.noContent().build());

        // When & Then
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(itemClient, times(1)).deleteItemById(eq(userId), eq(itemId));
    }

    @Test
    @DisplayName("Поиск вещей по тексту — успешный ответ")
    void searchItem_Should_Return_Search_Results_Test() throws Exception {
        // Given
        String text = "test";
        when(itemClient.searchItem(text))
                .thenReturn(ResponseEntity.ok().body("[\"SearchResult1\", \"SearchResult2\"]"));

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"SearchResult1\", \"SearchResult2\"]"));

        verify(itemClient, times(1)).searchItem(text);
    }

    @Test
    @DisplayName("Добавление комментария к вещи — успешный ответ")
    void commentItemById_Should_Return_Comment_Test() throws Exception {
        // Given
        long userId = 1L;
        long itemId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("Test comment");

        when(itemClient.commentItemById(eq(userId), eq(itemId), any(NewCommentDto.class)))
                .thenReturn(ResponseEntity.ok().body("Created comment"));

        // When & Then
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Created comment"));

        verify(itemClient, times(1)).commentItemById(eq(userId), eq(itemId), any(NewCommentDto.class));
    }
}
