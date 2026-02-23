package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemShortDto;
import ru.practicum.shareit.server.item.dto.ItemShortProjection;
import ru.practicum.shareit.server.request.dto.NewRequestItemDto;
import ru.practicum.shareit.server.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.server.request.mapper.RequestItemMapper;
import ru.practicum.shareit.server.request.model.RequestItem;
import ru.practicum.shareit.server.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestItemServiceImpl implements RequestItemService{
    private static final String REQUEST_NOT_FOUND = "Запрос с id = %d не найден.";
    private final RequestItemRepository requestItemRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public RequestItem createRequestItem(long userId, NewRequestItemDto newRequestItemDto) {
        log.info("Добавление пользователем {} нового запроса: {}", userId, newRequestItemDto);
        RequestItem newRequestItem = RequestItemMapper.toRequestItem(newRequestItemDto);
        newRequestItem.setRequestor(userService.getUserById(userId));
        newRequestItem = requestItemRepository.save(newRequestItem);
        log.info("Добавлен новый запрос: {}", newRequestItem);
        return newRequestItem;
    }

    @Override
    public RequestItem getRequestItemById(long requestId) {
        log.info("Получаем запрос {}", requestId);
        return getRequestItemOrThrow(requestId);
    }

    @Override
    public RequestItemResponseDto getRequestItemResponseDto(RequestItem request) {
        log.info("Конвертируем в дто: {}.", request);
        Long requestId = request.getId();
        List<ItemShortProjection> itemProjections = itemRepository.findItemsByRequestId(requestId);

        List<ItemShortDto> itemsShortDto = itemProjections.stream()
                .map(projection -> ItemShortDto.builder()
                        .id(projection.getId())
                        .name(projection.getName())
                        .ownerId(projection.getOwnerId())
                        .build())
                .collect(Collectors.toList());

        RequestItemResponseDto requestItemResponseDto = RequestItemMapper.toRequestItemResponseDto(request);
        requestItemResponseDto.setItems(itemsShortDto);
        return requestItemResponseDto;
    }

    @Override
    public List<RequestItemResponseDto> getAllRequestItemByRequestor(long userId, Integer page, Integer size) {
        log.info("Получаем все запросы созданные пользователем {}, page={}, size={}", userId, page, size);
        userService.checkUserExists(userId);
        Pageable pageable = PageRequest.of(page, size);
        List<RequestItem> requests = requestItemRepository.findByRequestorId(userId, pageable).getContent();
        return buildListRequestItemResponseDto(requests);
    }

    @Override
    public List<RequestItemResponseDto> getAllRequestItemByOtherUser(long userId, Integer page, Integer size) {
        log.info("Получаем для пользователя {} все запросы созданные другими пользователями, page={}, size={}",
                userId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<RequestItem> requests = requestItemRepository.findRequestItemByOtherUser(userId, pageable).getContent();
        return buildListRequestItemResponseDto(requests);
    }

    private List<RequestItemResponseDto> buildListRequestItemResponseDto(List<RequestItem> requests) {
        List<Long> requestIds = requests.stream()
                .map(RequestItem::getId)
                .collect(Collectors.toList());

        if (requestIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemShortProjection> itemProjections = itemRepository.findItemsByRequestIds(requestIds);

        Map<Long, List<ItemShortDto>> itemsShortDto = itemProjections.stream()
                .collect(Collectors.groupingBy(
                        projection -> projection.getRequestId(),
                        Collectors.mapping(
                                projection -> ItemShortDto.builder()
                                        .id(projection.getId())
                                        .name(projection.getName())
                                        .ownerId(projection.getOwnerId())
                                        .build(),
                                Collectors.toList()
                        )
                ));

        return requests.stream()
                .map(request -> {
                    List<ItemShortDto> itemsForRequest = itemsShortDto.getOrDefault(request.getId(), Collections.emptyList());
                    return RequestItemResponseDto.builder()
                            .id(request.getId())
                            .description(request.getDescription())
                            .created(request.getCreated())
                            .requestorId(request.getRequestor().getId())
                            .items(itemsForRequest)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private RequestItem getRequestItemOrThrow(Long requestId) {
        log.info("Получение запроса ID {} (или ошибки)).", requestId);
        return requestItemRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(REQUEST_NOT_FOUND, requestId)));
    }
}
