package ru.practicum.shareit.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.request.dto.NewRequestItemDto;
import ru.practicum.shareit.server.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.server.request.model.RequestItem;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestItemController.class)
@DisplayName("Тесты для RequestItemController")
class RequestItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestItemService requestItemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание запроса на вещь — успешный ответ")
    void createRequestItem_Should_Return_Created_Test() throws Exception {
        // Given
        long userId = 1L;
        NewRequestItemDto newRequestItemDto = new NewRequestItemDto();
        newRequestItemDto.setDescription("Test request description");

        RequestItemResponseDto requestItemResponseDto = new RequestItemResponseDto();
        requestItemResponseDto.setId(1L);
        requestItemResponseDto.setDescription("Test request description");
        requestItemResponseDto.setCreated(java.time.LocalDateTime.now());

        when(requestItemService.createRequestItem(eq(userId), any(NewRequestItemDto.class)))
                .thenReturn(new RequestItem());
        when(requestItemService.getRequestItemResponseDto(any(RequestItem.class)))
                .thenReturn(requestItemResponseDto);

        // When & Then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequestItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test request description"))
                .andExpect(jsonPath("$.created").exists());

        verify(requestItemService, times(1)).createRequestItem(eq(userId), any(NewRequestItemDto.class));
    }

    @Test
    @DisplayName("Получение запроса по ID — успешный ответ")
    void getRequestItemById_Should_Return_Request_Test() throws Exception {
        // Given
        long requestId = 1L;

        RequestItemResponseDto requestItemResponseDto = new RequestItemResponseDto();
        requestItemResponseDto.setId(requestId);
        requestItemResponseDto.setDescription("Existing request description");
        requestItemResponseDto.setCreated(java.time.LocalDateTime.now());

        when(requestItemService.getRequestItemById(eq(requestId)))
                .thenReturn(new RequestItem());
        when(requestItemService.getRequestItemResponseDto(any(RequestItem.class)))
                .thenReturn(requestItemResponseDto);

        // When & Then
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Existing request description"))
                .andExpect(jsonPath("$.created").exists());

        verify(requestItemService, times(1)).getRequestItemById(eq(requestId));
    }

    @Test
    @DisplayName("Получение всех запросов пользователя — успешный ответ с пагинацией")
    void getAllRequestItemByRequestor_Should_Return_All_Requests_With_Pagination_Test() throws Exception {
        // Given
        long userId = 1L;
        int page = 0;
        int size = 10;

        RequestItemResponseDto request1 = new RequestItemResponseDto();
        request1.setId(1L);
        request1.setDescription("Request 1");
        request1.setCreated(java.time.LocalDateTime.now());

        RequestItemResponseDto request2 = new RequestItemResponseDto();
        request2.setId(2L);
        request2.setDescription("Request 2");
        request2.setCreated(java.time.LocalDateTime.now());

        List<RequestItemResponseDto> requests = List.of(request1, request2);

        when(requestItemService.getAllRequestItemByRequestor(eq(userId), eq(page), eq(size)))
                .thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Request 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Request 2"));

        verify(requestItemService, times(1)).getAllRequestItemByRequestor(eq(userId), eq(page), eq(size));
    }

    @Test
    @DisplayName("Получение всех запросов других пользователей — успешный ответ с пагинацией")
    void getAllRequestItemByOtherUser_Should_Return_All_Requests_For_Other_Users_With_Pagination_Test() throws Exception {
        // Given
        long userId = 1L;
        int page = 0;
        int size = 10;

        RequestItemResponseDto request1 = new RequestItemResponseDto();
        request1.setId(1L);
        request1.setDescription("Other user request 1");
        request1.setCreated(java.time.LocalDateTime.now());

        RequestItemResponseDto request2 = new RequestItemResponseDto();
        request2.setId(2L);
        request2.setDescription("Other user request 2");
        request2.setCreated(java.time.LocalDateTime.now());

        List<RequestItemResponseDto> requests = List.of(request1, request2);

        when(requestItemService.getAllRequestItemByOtherUser(eq(userId), eq(page), eq(size)))
                .thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Other user request 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Other user request 2"));

        verify(requestItemService, times(1)).getAllRequestItemByOtherUser(eq(userId), eq(page), eq(size));
    }
}
