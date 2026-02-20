package ru.practicum.shareit.server.request;

import ru.practicum.shareit.server.request.dto.NewRequestItemDto;
import ru.practicum.shareit.server.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.server.request.model.RequestItem;

import java.util.List;

public interface RequestItemService {

    RequestItem createRequestItem(long userId, NewRequestItemDto newRequestItemDto);

    RequestItem getRequestItemById(long requestId);

    RequestItemResponseDto getRequestItemResponseDto(RequestItem request);

    List<RequestItemResponseDto> getAllRequestItemByRequestor(long userId, Integer page, Integer size);

    List<RequestItemResponseDto> getAllRequestItemByOtherUser(long userId, Integer page, Integer size);

}
