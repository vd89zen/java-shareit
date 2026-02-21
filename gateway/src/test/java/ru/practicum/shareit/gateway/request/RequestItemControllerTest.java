package ru.practicum.shareit.gateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.request.dto.NewRequestItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestItemController.class)
@DisplayName("Тесты для RequestItemController")
class RequestItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestItemClient requestItemClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final long USER_ID = 1L;
    private static final int PAGE = 0;
    private static final int SIZE = 10;

    @Test
    @DisplayName("Создание запроса на вещь — успешный ответ")
    void createRequestItem_Should_Return_Created_Test() throws Exception {
        // Given
        NewRequestItemDto newRequestItemDto = new NewRequestItemDto();
        newRequestItemDto.setDescription("Test request");

        when(requestItemClient.createRequestItem(eq(USER_ID), any(NewRequestItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequestItemDto)))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).createRequestItem(eq(USER_ID), any(NewRequestItemDto.class));
    }

    @Test
    @DisplayName("Получение запроса по ID — успешный ответ")
    void getRequestItemById_Should_Return_Request_Test() throws Exception {
        // Given
        long requestId = 1L;
        when(requestItemClient.getRequestItemById(requestId))
                .thenReturn(ResponseEntity.ok().body("Request data"));

        // When & Then
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(content().string("Request data"));

        verify(requestItemClient, times(1)).getRequestItemById(requestId);
    }

    @Test
    @DisplayName("Получение всех запросов пользователя — успешный ответ с пагинацией")
    void getAllRequestItemByRequestor_Should_Return_All_Requests_With_Pagination_Test() throws Exception {
        // Given
        when(requestItemClient.getAllRequestItemByRequestor(eq(USER_ID), eq(PAGE), eq(SIZE)))
                .thenReturn(ResponseEntity.ok().body("[\"Request1\", \"Request2\"]"));

        // When & Then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Request1\", \"Request2\"]"));

        verify(requestItemClient, times(1)).getAllRequestItemByRequestor(eq(USER_ID), eq(PAGE), eq(SIZE));
    }

    @Test
    @DisplayName("Получение всех запросов другими пользователями — успешный ответ с пагинацией")
    void getAllRequestItemByOtherUser_Should_Return_All_Requests_From_Other_Users_With_Pagination_Test() throws Exception {
        // Given
        when(requestItemClient.getAllRequestItemByOtherUser(eq(USER_ID), eq(PAGE), eq(SIZE)))
                .thenReturn(ResponseEntity.ok().body("[\"OtherRequest1\", \"OtherRequest2\"]"));

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"OtherRequest1\", \"OtherRequest2\"]"));

        verify(requestItemClient, times(1)).getAllRequestItemByOtherUser(eq(USER_ID), eq(PAGE), eq(SIZE));
    }
}
