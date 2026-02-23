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

    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 1L;
    private static final int PAGE = 0;
    private static final int SIZE = 10;

    @Test
    @DisplayName("Создание вещи — успешный ответ")
    void createItem_Should_Return_Created_Test() throws Exception {
        // Given
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Test Item");
        newItemDto.setDescription("Test description");
        newItemDto.setAvailable(true);
        newItemDto.setRequestId(null);

        when(itemClient.createItem(eq(USER_ID), any(NewItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).createItem(eq(USER_ID), any(NewItemDto.class));
    }

    @Test
    @DisplayName("Получение вещи по ID — успешный ответ")
    void getItemById_Should_Return_Item_Test() throws Exception {
        // Given
        when(itemClient.getItemById(ITEM_ID))
                .thenReturn(ResponseEntity.ok().body("Item data"));

        // When & Then
        mockMvc.perform(get("/items/{itemId}", ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("Item data"));

        verify(itemClient, times(1)).getItemById(ITEM_ID);
    }

    @Test
    @DisplayName("Получение всех вещей владельца — успешный ответ с пагинацией")
    void getAllItemForOwner_Should_Return_All_Items_With_Pagination_Test() throws Exception {
        // Given
        when(itemClient.getAllItemForOwner(eq(USER_ID), eq(PAGE), eq(SIZE)))
                .thenReturn(ResponseEntity.ok().body("[\"Item1\", \"Item2\"]"));

        // When & Then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Item1\", \"Item2\"]"));

        verify(itemClient, times(1)).getAllItemForOwner(eq(USER_ID), eq(PAGE), eq(SIZE));
    }

    @Test
    @DisplayName("Обновление вещи по ID — успешный ответ")
    void updateItemById_Should_Return_Updated_Item_Test() throws Exception {
        // Given
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Name");

        when(itemClient.updateItemById(eq(USER_ID), eq(ITEM_ID), any(ItemUpdateDto.class)))
                .thenReturn(ResponseEntity.ok().body("Updated item"));

        // When & Then
        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated item"));

        verify(itemClient, times(1)).updateItemById(eq(USER_ID), eq(ITEM_ID), any(ItemUpdateDto.class));
    }

    @Test
    @DisplayName("Удаление вещи по ID — успешный ответ (No Content)")
    void deleteItemById_Should_Return_No_Content_Test() throws Exception {
        // Given
        when(itemClient.deleteItemById(eq(USER_ID), eq(ITEM_ID)))
                .thenReturn(ResponseEntity.noContent().build());

        // When & Then
        mockMvc.perform(delete("/items/{itemId}", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNoContent());

        verify(itemClient, times(1)).deleteItemById(eq(USER_ID), eq(ITEM_ID));
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
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("Test comment");

        when(itemClient.commentItemById(eq(USER_ID), eq(ITEM_ID), any(NewCommentDto.class)))
                .thenReturn(ResponseEntity.ok().body("Created comment"));

        // When & Then
        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Created comment"));

        verify(itemClient, times(1)).commentItemById(eq(USER_ID), eq(ITEM_ID), any(NewCommentDto.class));
    }
}
