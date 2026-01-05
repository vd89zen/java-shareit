package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

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
    public ResponseEntity<ItemDtoForResponse> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.create(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoForResponse> findById(@PathVariable @NotNull Long itemId) {
        return ResponseEntity
                .ok(itemService.findById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoForResponse>> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity
                .ok(itemService.findAll(userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoForResponse> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable @NotNull Long itemId,
                                                     @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        return ResponseEntity
                .ok(itemService.update(userId, itemId, itemUpdateDto));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoForResponse>> search(@RequestParam @NotNull String text) {
        return ResponseEntity
                .ok(itemService.search(text));
    }

}
