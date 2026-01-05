package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemDtoForResponse toItemDtoForResponse(Item item) {
        return ItemDtoForResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequestId() != null ?
                        ItemRequest.builder()
                                .id(itemDto.getRequestId())
                                .build()
                        : null)
                .build();
    }

    public static Item updateItemFields(Item updatingItem, ItemUpdateDto itemUpdateDto) {

        if (itemUpdateDto.hasName()) {
            updatingItem.setName(itemUpdateDto.getName());
        }

        if (itemUpdateDto.hasDescription()) {
            updatingItem.setDescription(itemUpdateDto.getDescription());
        }

        if (itemUpdateDto.hasAvailable()) {
            updatingItem.setAvailable(itemUpdateDto.getAvailable());
        }

        return updatingItem;
    }
}
