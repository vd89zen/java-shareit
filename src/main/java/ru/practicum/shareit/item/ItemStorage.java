package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item create(Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findAll(Long userId);

    void update(Item item);

    boolean delete(Long itemId);

    List<Item> search(String text);

    boolean isItemExists(Long itemId);

}
