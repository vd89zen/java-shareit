package ru.practicum.shareit.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.user.dto.NewUserDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(NewUserDto newUserDto) {
        String path = "";
        return post(path, newUserDto);
    }

    public ResponseEntity<Object> findUserById(long userId) {
        String path = String.format("/%d", userId);
        return get(path);
    }

    public ResponseEntity<Object> findAllUser() {
        String path = "";
        return get(path);
    }

    public ResponseEntity<Object> updateUser(long userId, UserUpdateDto userUpdateDto) {
        String path = String.format("/%d", userId);
        return patch(path, userUpdateDto);
    }

    public ResponseEntity<Object> deleteUserById(long userId) {
        String path = String.format("/%d", userId);
        return delete(path);
    }
}
