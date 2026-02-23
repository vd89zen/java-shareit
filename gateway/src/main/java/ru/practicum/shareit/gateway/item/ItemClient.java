package ru.practicum.shareit.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;
import ru.practicum.shareit.gateway.item.dto.NewCommentDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, NewItemDto newItemDto) {
        String path = "";
        return post(path, userId, newItemDto);
    }

    public ResponseEntity<Object> getItemById(long itemId) {
        String path = String.format("/%d", itemId);
        return get(path);
    }

    public ResponseEntity<Object> getAllItemForOwner(long userId, Integer page, Integer size) {
        String path = "?page={page}&size={size}";
        Map<String, Object> parameters = Map.of(
                "page", page,
                "size", size
        );
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> updateItemById(long userId, long itemId, ItemUpdateDto itemUpdateDto) {
        String path = String.format("/%d", itemId);
        return patch(path, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> deleteItemById(long userId, long itemId) {
        String path = String.format("/%d", itemId);
        return delete(path, userId);
    }

    public ResponseEntity<Object> searchItem(String text) {
        String path = String.format("/search?text=%s", text);
        return get(path);
    }

    public ResponseEntity<Object> commentItemById(long userId, long itemId, NewCommentDto newCommentDto) {
        String path = String.format("/%d/comment", itemId);
        return post(path, userId, newCommentDto);
    }
}
