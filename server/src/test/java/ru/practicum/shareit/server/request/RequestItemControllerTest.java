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

import java.time.LocalDateTime;
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

    private static final long USER_ID = 1L;
    private static final int PAGE = 0;
    private static final int SIZE = 10;

    private RequestItemResponseDto createRequestItemResponseDto(long id, String description) {
        RequestItemResponseDto dto = new RequestItemResponseDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

    @Test
    @DisplayName("Создание запроса на вещь — успешный ответ")
    void createRequestItem_Should_Return_Created_Test() throws Exception {
        // Given
        NewRequestItemDto newRequestItemDto = new NewRequestItemDto();
        newRequestItemDto.setDescription("Test request description");

        RequestItemResponseDto requestItemResponseDto =
                createRequestItemResponseDto(1L,"Test request description");

        when(requestItemService.createRequestItem(eq(USER_ID), any(NewRequestItemDto.class)))
                .thenReturn(new RequestItem());
        when(requestItemService.getRequestItemResponseDto(any(RequestItem.class)))
                .thenReturn(requestItemResponseDto);

        // When & Then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequestItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test request description"))
                .andExpect(jsonPath("$.created").exists());

        verify(requestItemService, times(1))
                .createRequestItem(eq(USER_ID), any(NewRequestItemDto.class));
    }

    @Test
    @DisplayName("Получение запроса по ID — успешный ответ")
    void getRequestItemById_Should_Return_Request_Test() throws Exception {
        // Given
        long requestId = 1L;

        RequestItemResponseDto requestItemResponseDto =
                createRequestItemResponseDto(requestId,"Existing request description");

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
        RequestItemResponseDto request1 = createRequestItemResponseDto(1L, "Request 1");
        RequestItemResponseDto request2 = createRequestItemResponseDto(2L, "Request 2");

        List<RequestItemResponseDto> requests = List.of(request1, request2);

        when(requestItemService.getAllRequestItemByRequestor(eq(USER_ID), eq(PAGE), eq(SIZE)))
                .thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Request 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Request 2"));


        verify(requestItemService, times(1))
                .getAllRequestItemByRequestor(eq(USER_ID), eq(PAGE), eq(SIZE));
    }

    @Test
    @DisplayName("Получение всех запросов других пользователей — успешный ответ с пагинацией")
    void getAllRequestItemByOtherUser_Should_Return_All_Requests_For_Other_Users_With_Pagination_Test() throws Exception {
        // Given
        RequestItemResponseDto request1 = createRequestItemResponseDto(1L, "Other user request 1");
        RequestItemResponseDto request2 = createRequestItemResponseDto(2L, "Other user request 2");

        List<RequestItemResponseDto> requests = List.of(request1, request2);

        when(requestItemService.getAllRequestItemByOtherUser(eq(USER_ID), eq(PAGE), eq(SIZE)))
                .thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Other user request 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Other user request 2"));

        verify(requestItemService, times(1))
                .getAllRequestItemByOtherUser(eq(USER_ID), eq(PAGE), eq(SIZE));
    }
}
