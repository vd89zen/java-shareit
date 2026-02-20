package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;
import ru.practicum.shareit.gateway.item.dto.NewCommentDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @Valid @RequestBody NewItemDto newItemDto) {
        log.info("Creating item {}", newItemDto);
        return itemClient.createItem(userId, newItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @NotNull Long itemId) {
        log.info("Getting item {}", itemId);
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PositiveOrZero @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting all item for owner {}, page={}, size={}", userId);
        return itemClient.getAllItemForOwner(userId, page, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Updating item {} user {}: {}", itemId, userId, itemUpdateDto);
        return itemClient.updateItemById(userId, itemId, itemUpdateDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId) {
        log.info("Deleting item {} user {}", itemId, userId);
        return itemClient.deleteItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam @NotNull String text) {
        log.info("Searching item by text: {}", text);
        return itemClient.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> commentItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable long itemId,
                                                   @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Commenting item {} user {}: {}", itemId, userId, newCommentDto);
        return itemClient.commentItemById(userId, itemId, newCommentDto);
    }
}
