package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.NewRequestItemDto;
import ru.practicum.shareit.server.request.dto.RequestItemResponseDto;

import java.util.List;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestItemController {
    private final RequestItemService requestItemService;

    @PostMapping
    public ResponseEntity<RequestItemResponseDto> createRequestItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                    @RequestBody NewRequestItemDto newRequestItemDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        requestItemService.getRequestItemResponseDto(
                                requestItemService.createRequestItem(userId, newRequestItemDto))
                );
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestItemResponseDto> getRequestItemById(@PathVariable long requestId) {
        return ResponseEntity
                .ok(
                        requestItemService.getRequestItemResponseDto(
                                requestItemService.getRequestItemById(requestId))
                );
    }

    @GetMapping
    public ResponseEntity<List<RequestItemResponseDto>> getAllRequestItemByRequestor(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity
                .ok(
                        requestItemService.getAllRequestItemByRequestor(userId, page, size)
                );
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestItemResponseDto>> getAllRequestItemByOtherUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity
                .ok(
                        requestItemService.getAllRequestItemByOtherUser(userId, page, size)
                );
    }
}
