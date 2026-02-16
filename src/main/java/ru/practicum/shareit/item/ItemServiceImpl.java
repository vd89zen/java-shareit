package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemBookingDatesDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private static final String ITEM_NOT_FOUND = "Вещь с id = %d не найдена.";
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;


    @Override
    @Transactional
    public Item create(Long ownerId, ItemDto itemDto) {
        log.info("Добавление пользователем {} новой вещи: {}.", ownerId, itemDto);

        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userService.findById(ownerId));

        //в последующем добавится проверка существования request
        if (itemDto.getRequestId() != null) {
            //временная заглушка, до реализации ТЗ следующих спринтов
            newItem.setRequest(ItemRequest.builder().id(itemDto.getRequestId()).build());
        }

        newItem = itemRepository.save(newItem);
        log.info("Добавлена новая вещь: {}.", newItem);
        return newItem;
    }

    @Override
    public Item findById(Long itemId) {
        log.info("Поиск вещи ID {}.", itemId);
        return getItemOrThrow(itemId);
    }

    @Override
    public List<Item> findAll(Long ownerId) {
        log.info("Поиск вещей пользователя ID {}.", ownerId);
        return itemRepository.findAllByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public Item update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto) {
        log.info("Обновление вещи ID {} пользователя {}: {}.", itemId, ownerId, itemUpdateDto);
        userService.checkUserExists(ownerId);
        Item updatingItem = checkItemOwner(ownerId, itemId);
        ItemMapper.updateItemFields(updatingItem, itemUpdateDto);
        log.info("Обновлена вещь ID {} пользователя {}: {}.", itemId, ownerId, itemUpdateDto);
        return itemRepository.save(updatingItem);
    }

    @Override
    @Transactional
    public Item updateItemAvailable(Item item, Boolean available) {
        log.info("Обновление доступности вещи {}: {}.", item, available);
        item.setAvailable(available);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Long ownerId, Long itemId) {
        log.info("Удаление вещи ID {} пользователя {}.", ownerId, itemId);
        checkItemOwner(ownerId, itemId);
        itemRepository.deleteById(itemId);
        log.info("Удалена вещь ID {} пользователя {}.", ownerId, itemId);
    }

    @Override
    public List<Item> search(String text) {
        log.info("Поиск вещей, имя или описание которых содержат: {}.", text);
        final String normalizedText = text.trim().toLowerCase(Locale.ROOT);
        if (normalizedText.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(normalizedText);
    }

    @Override
    public void checkItemExists(Long itemId) {
        log.info("Проверка существования вещи ID {}.", itemId);
        if (itemRepository.existsById(itemId) == false) {
            throw new NotFoundException(String.format(ITEM_NOT_FOUND, itemId));
        }
        log.info("Вещь ID {} существует.", itemId);
    }

    @Override
    public Item checkItemOwner(Long ownerId, Long itemId) {
        log.info("Проверяем владеет ли пользователь ID {} вещью ID {}.", ownerId, itemId);
        Item item = getItemOrThrow(itemId);
        if (item.getOwner().getId().equals(ownerId) == false) {
            throw new WrongOwnerException(
                    String.format("Пользователь ID %d не является владельцем вещи ID %d.", ownerId, itemId));
        }
        log.info("Подтверждено владение пользователя ID {} вещью {}.", ownerId, item);
        return item;
    }

    private Item getItemOrThrow(Long itemId) {
        log.info("Получение вещи ID {} (или ошибки)).", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
    }

    @Override
    @Transactional
    public Comment comment(Long userId, Long itemId, NewCommentDto newCommentDto) {
        log.info("Добавление комментария пользователя ID {} для вещи ID {}.", userId, itemId);
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        if (bookingRepository.checkBookingItemForComment(itemId, userId, currentTime, Status.APPROVED) == false) {
            throw new WrongRequestException(
                    String.format("У пользователя ID %d нет завершённых бронирований вещи ID %d.", userId, itemId));
        }
        Comment comment = CommentMapper.toComment(newCommentDto);
        comment.setItem(findById(itemId));
        comment.setAuthor(userService.findById(userId));
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        log.debug("ВРЕМЯ {}. Бронирование {}.", LocalDateTime.now(),
                bookingRepository.findByBookerIdPastBookings(userId, List.of(Status.APPROVED, Status.CANCELED)));
        log.info("Добавлен комментарий {}.", comment);
        return commentRepository.save(comment);
    }

    @Override
    public CommentResponseDto getCommentResponseDto(Comment comment) {
        return CommentMapper.toCommentResponseDto(comment);
    }

    @Override
    public ItemResponseDto getItemResponseDto(Item item) {
        log.info("Конвертируем в дто: {}.", item);
        Long itemId = item.getId();
        ItemResponseDto dto = ItemMapper.toItemResponseDto(item);
        dto.setComments(CommentMapper.toListCommentResponseDto(commentRepository.findAllByItemId(itemId)));
        log.info("Сконвертировали: {}.", dto);
        return dto;
    }

    @Override
    public List<ItemResponseDto> getListItemResponseDto(List<Item> items) {
        log.info("Конвертируем списком в дто: {}.", items);
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findAllByItemIds(itemIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId()
                ));
        return items.stream()
                .map(item -> {
                    ItemResponseDto dto = ItemMapper.toItemResponseDto(item);
                    Long id = dto.getId();

                    dto.setComments(CommentMapper.toListCommentResponseDto(
                            commentsByItemId.getOrDefault(id, Collections.emptyList())));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public ItemResponseForOwnerDto getItemResponseForOwnerDto(Item item) {
        log.info("Конвертируем в дто(for owner): {}.", item);
        Long itemId = item.getId();
        ItemResponseForOwnerDto dto = ItemMapper.toItemResponseForOwnerDto(item);
        dto.setComments(CommentMapper.toListCommentResponseDto(commentRepository.findAllByItemId(itemId)));
        ItemBookingDatesDto datesDto = bookingRepository.findBookingDatesByItemId(itemId);
        if (datesDto != null) {
            dto.setLastBooking(datesDto.getLastBooking());
            dto.setNextBooking(datesDto.getNextBooking());
        }
        log.info("Сконвертировали (for owner): {}.", dto);
        return dto;
    }

    @Override
    public List<ItemResponseForOwnerDto> getListItemResponseForOwnerDto(List<Item> items) {
        log.info("Конвертируем списком в дто(for owner): {}.", items);
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findAllByItemIds(itemIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId()
                ));

        Map<Long, ItemBookingDatesDto> datesMap = bookingRepository.findBookingDatesByItemIds(itemIds).stream()
                .collect(Collectors.toMap(ItemBookingDatesDto::getItemId, dto -> dto));

        return items.stream()
                .map(item -> {
                    ItemResponseForOwnerDto itemDto = ItemMapper.toItemResponseForOwnerDto(item);
                    Long id = itemDto.getId();

                    itemDto.setComments(CommentMapper.toListCommentResponseDto(
                            commentsByItemId.getOrDefault(id, Collections.emptyList())));

                    ItemBookingDatesDto datesDto = datesMap.get(id);
                    if (datesDto != null) {
                        itemDto.setLastBooking(datesDto.getLastBooking());
                        itemDto.setNextBooking(datesDto.getNextBooking());
                    }
                    return itemDto;
                }).collect(Collectors.toList());
    }
}
