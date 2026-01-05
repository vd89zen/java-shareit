package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationError;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final String ITEM_NOT_FOUND = "Вещь с id = %d не найдена.";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final UserService userService;

    @Override
    public ItemDtoForResponse create(Long userId, ItemDto itemDto) {
        log.info("Добавление новой вещи: {}.", itemDto);
        userService.checkUserExists(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userStorage.findById(userId).get());

        //в последующем добавится проверка существования request
        if (itemDto.getRequestId() != null) {
            //временная заглушка, до реализации ТЗ следующих спринтов
            item.setRequest(ItemRequest.builder().id(itemDto.getRequestId()).build());
        }

        item = itemStorage.create(item);

        return ItemMapper.toItemDtoForResponse(item);
    }

    @Override
    public ItemDtoForResponse findById(Long itemId) {
        log.info("Поиск вещи ID {}.", itemId);
        return ItemMapper.toItemDtoForResponse(getItemOrThrow(itemId));
    }

    @Override
    public List<ItemDtoForResponse> findAll(Long userId) {
        log.info("Поиск вещей пользователя ID {}.", userId);
        return itemStorage.findAll(userId).stream()
                .map(ItemMapper::toItemDtoForResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ItemDtoForResponse update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        log.info("Обновление вещи ID {} пользователя {}: {}.", itemId, userId, itemUpdateDto);
        userService.checkUserExists(userId);
        Item updatingItem = getItemOrThrow(itemId);

        if (updatingItem.getOwner().getId() != userId) {
            throw new ValidationException(ValidationError.builder()
                    .field("owner")
                    .message(String.format("Пользователь ID %d не является владельцем вещи ID %d.", userId, itemId))
                    .rejectedValue(userId)
                    .build());
        }

        ItemMapper.updateItemFields(updatingItem, itemUpdateDto);
        itemStorage.update(updatingItem);
        return ItemMapper.toItemDtoForResponse(updatingItem);
    }

    @Override
    public void delete(Long userId, Long itemId) {
        log.info("Удаление вещи ID {}.", itemId);
        if (itemStorage.delete(itemId) == false) {
            throw new NotFoundException(String.format(ITEM_NOT_FOUND, userId));
        }
    }

    @Override
    public List<ItemDtoForResponse> search(String text) {
        log.info("Поиск вещей, имя или описание которых содержат: {}.", text);
        if (text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDtoForResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void checkItemExists(Long itemId) {
        log.info("Проверка существования вещи ID {}.", itemId);
        if (itemStorage.isItemExists(itemId) == false) {
            throw new NotFoundException(String.format(ITEM_NOT_FOUND, itemId));
        }
    }

    private Item getItemOrThrow(Long itemId) {
        log.debug("Получение вещи ID {}.", itemId);
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
    }
}
