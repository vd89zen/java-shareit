package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Long ownerId, ItemDto itemDto);

    Item findById(Long itemId);

    List<Item> findAll(Long ownerId);

    Item update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto);

    Item updateItemAvailable(Item item, Boolean available);

    void delete(Long ownerId, Long itemId);

    List<Item> search(String text);

    Comment comment(Long userId, Long itemId, NewCommentDto newCommentDto);

    void checkItemExists(Long itemId);

    Item checkItemOwner(Long ownerId, Long itemId);

    CommentResponseDto getCommentResponseDto(Comment comment);

    ItemResponseDto getItemResponseDto(Item item);

    List<ItemResponseDto> getListItemResponseDto(List<Item> items);

    ItemResponseForOwnerDto getItemResponseForOwnerDto(Item item);

    List<ItemResponseForOwnerDto> getListItemResponseForOwnerDto(List<Item> items);
}
