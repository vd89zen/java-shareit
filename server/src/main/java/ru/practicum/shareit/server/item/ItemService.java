package ru.practicum.shareit.server.item;

import ru.practicum.shareit.server.item.dto.*;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Long ownerId, NewItemDto newItemDto);

    Item getItemById(Long itemId);

    List<Item> getAllItemForOwner(Long ownerId);

    Item updateItemById(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto);

    Item updateItemAvailable(Item item, Boolean available);

    void deleteItemById(Long ownerId, Long itemId);

    List<Item> searchItem(String text);

    Comment commentItemById(Long userId, Long itemId, NewCommentDto newCommentDto);

    void checkItemExists(Long itemId);

    Item checkItemOwner(Long ownerId, Long itemId);

    CommentResponseDto getCommentResponseDto(Comment comment);

    ItemResponseDto getItemResponseDto(Item item);

    List<ItemResponseDto> getListItemResponseDto(List<Item> items);

    ItemResponseForOwnerDto getItemResponseForOwnerDto(Item item);

    List<ItemResponseForOwnerDto> getListItemResponseForOwnerDto(List<Item> items);
}
