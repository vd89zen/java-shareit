package ru.practicum.shareit.server.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.dto.*;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.mapper.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(UserMapper.toUserResponseDto(item.getOwner()))
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemResponseForOwnerDto toItemResponseForOwnerDto(Item item) {
        return ItemResponseForOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(NewItemDto newItemDto) {
        return Item.builder()
                .name(newItemDto.getName())
                .description(newItemDto.getDescription())
                .available(newItemDto.getAvailable())
                .build();
    }

    public static void updateItemFields(Item updatingItem, ItemUpdateDto itemUpdateDto) {

        if (itemUpdateDto.hasName()) {
            updatingItem.setName(itemUpdateDto.getName());
        }

        if (itemUpdateDto.hasDescription()) {
            updatingItem.setDescription(itemUpdateDto.getDescription());
        }

        if (itemUpdateDto.hasAvailable()) {
            updatingItem.setAvailable(itemUpdateDto.getAvailable());
        }

    }
}
