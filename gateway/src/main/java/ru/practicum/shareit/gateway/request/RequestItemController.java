package ru.practicum.shareit.gateway.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.request.dto.NewRequestItemDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestItemController {
    private final RequestItemClient requestItemClient;

    @PostMapping
    public ResponseEntity<Object> createRequestItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody NewRequestItemDto newRequestItemDto) {
        log.info("Creating request item {}", newRequestItemDto);
        return requestItemClient.createRequestItem(userId, newRequestItemDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestItemById(@PathVariable long requestId) {
        log.info("Getting request item {}", requestId);
        return requestItemClient.getRequestItemById(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestItemByRequestor(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting all request item by requestor {}, page={}, size={}", userId, page, size);
        return requestItemClient.getAllRequestItemByRequestor(userId, page, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestItemByOtherUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting for user {} all request item by other user, page={}, size={}", userId, page, size);
        return requestItemClient.getAllRequestItemByOtherUser(userId, page, size);
    }
}
