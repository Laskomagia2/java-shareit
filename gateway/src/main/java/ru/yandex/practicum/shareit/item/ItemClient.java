package ru.yandex.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.shareit.client.BaseClient;
import ru.yandex.practicum.shareit.item.dto.CommentRequest;
import ru.yandex.practicum.shareit.item.dto.ItemCreateRequest;
import ru.yandex.practicum.shareit.item.dto.ItemUpdateRequest;

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

    public ResponseEntity<Object> postItem(Long userId, ItemCreateRequest requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> postComment(Long userId, Long itemId, CommentRequest request) {
        return post("/" + itemId + "/comment", userId, request);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateRequest request) {
        return patch("/" + itemId, userId, request);
    }

    public ResponseEntity<Object> getItemsByOwner(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByDescription(Long userId, String desc) {
        Map<String, Object> parameters = Map.of("text", desc);
        return get("/search?text={text}", userId, parameters);
    }

}
