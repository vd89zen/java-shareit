package ru.practicum.shareit.gateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.request.dto.NewRequestItemDto;

import java.util.Map;

@Service
public class RequestItemClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequestItem(long userId, NewRequestItemDto newRequestItemDto) {
        String path = "";
        return post(path, userId, newRequestItemDto);
    }

    public ResponseEntity<Object> getRequestItemById(long requestId) {
        String path = String.format("/%d", requestId);
        return get(path);
    }

    public ResponseEntity<Object> getAllRequestItemByRequestor(long userId, Integer page, Integer size) {
        String path = "?page={page}&size={size}";
        Map<String, Object> parameters = Map.of(
                "page", page,
                "size", size
        );
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getAllRequestItemByOtherUser(long userId, Integer page, Integer size) {
        String path = "/all?page={page}&size={size}";
        Map<String, Object> parameters = Map.of(
                "page", page,
                "size", size
        );
        return get(path, userId, parameters);
    }

}
