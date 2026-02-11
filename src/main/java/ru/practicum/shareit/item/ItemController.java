package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> create(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                  @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        itemService.getItemResponseDto(
                                itemService.create(userId, itemDto))
                );
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> findById(@PathVariable @NotNull Long itemId) {
        return ResponseEntity
                .ok(
                        itemService.getItemResponseDto(
                                itemService.findById(itemId))
                );
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseForOwnerDto>> findAllForOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return ResponseEntity
                .ok(
                        itemService.getListItemResponseForOwnerDto(
                                itemService.findAll(userId))
                );
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> update(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                  @PathVariable @NotNull Long itemId,
                                                  @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        return ResponseEntity
                .ok(
                        itemService.getItemResponseDto(
                                itemService.update(userId, itemId, itemUpdateDto))
                );
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @PathVariable @NotNull Long itemId) {
        itemService.delete(userId, itemId);
        return ResponseEntity
                .noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> search(@RequestParam @NotNull String text) {
        return ResponseEntity
                .ok(
                        itemService.getListItemResponseDto(
                                itemService.search(text))
                );
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> comment(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                   @PathVariable @NotNull Long itemId,
                                                   @Valid @RequestBody NewCommentDto newCommentDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        CommentMapper.toCommentResponseDto(
                                itemService.comment(userId, itemId, newCommentDto))
                );
    }
}
