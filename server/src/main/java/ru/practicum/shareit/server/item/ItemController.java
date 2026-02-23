package ru.practicum.shareit.server.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestBody NewItemDto newItemDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        itemService.getItemResponseDto(
                                itemService.createItem(userId, newItemDto))
                );
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable long itemId) {
        return ResponseEntity
                .ok(
                        itemService.getItemResponseDto(
                                itemService.getItemById(itemId))
                );
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseForOwnerDto>> getAllItemForOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity
                .ok(
                        itemService.getListItemResponseForOwnerDto(
                                itemService.getAllItemForOwner(userId))
                );
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long itemId,
                                                  @RequestBody ItemUpdateDto itemUpdateDto) {
        return ResponseEntity
                .ok(
                        itemService.getItemResponseDto(
                                itemService.updateItemById(userId, itemId, itemUpdateDto))
                );
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId) {
        itemService.deleteItemById(userId, itemId);
        return ResponseEntity
                .noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItem(@RequestParam String text) {
        return ResponseEntity
                .ok(
                        itemService.getListItemResponseDto(
                                itemService.searchItem(text))
                );
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> commentItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable long itemId,
                                                   @RequestBody NewCommentDto newCommentDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        itemService.getCommentResponseDto(
                                itemService.commentItemById(userId, itemId, newCommentDto))
                );
    }
}
