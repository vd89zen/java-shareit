package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDtoForResponse create(Long userId, ItemDto itemDto);

    ItemDtoForResponse findById(Long itemId);

    List<ItemDtoForResponse> findAll(Long userId);

    ItemDtoForResponse update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    void delete(Long userId, Long itemId);

    List<ItemDtoForResponse> search(String text);

    void checkItemExists(Long itemId);
}
