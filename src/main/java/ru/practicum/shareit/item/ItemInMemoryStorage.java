package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ItemInMemoryStorage implements ItemStorage {
    private static Long nextId = 0L;
    private final ConcurrentHashMap<Long, Item> items = new ConcurrentHashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        items.put(nextId, item);

        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .map(item -> Item.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .owner(item.getOwner())
                        .request(item.getRequest())
                        .build());
    }

    @Override
    public List<Item> findAll(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public boolean delete(Long itemId) {
        if (items.remove(itemId) == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<Item> search(String text) {
        final String searchText = text.trim().toLowerCase();

        return items.values().stream()
                .filter(item -> item.getAvailable())
                .filter(item ->
                        item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean isItemExists(Long itemId) {
        return items.containsKey(itemId);
    }

    private synchronized long getNextId() {
        return ++nextId;
    }
}
