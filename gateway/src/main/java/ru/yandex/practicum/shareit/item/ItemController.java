package ru.yandex.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.shareit.item.dto.CommentRequest;
import ru.yandex.practicum.shareit.item.dto.ItemCreateRequest;
import ru.yandex.practicum.shareit.item.dto.ItemUpdateRequest;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postItem(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemCreateRequest request
    ) {
        log.info("Creating item {}, userId={}", request, userId);
        return itemClient.postItem(userId, request);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postCommentForItem(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable @Positive Long itemId,
            @Valid @RequestBody CommentRequest request
    ) {
        log.info("Creating comment {}, userId={}, itemId={}", request, userId, itemId);
        return itemClient.postComment(userId, itemId, request);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemUpdateRequest request,
            @PathVariable @Positive Long itemId
    ) {
        log.info("Update item {}, userId={}", request, userId);
        return itemClient.updateItem(userId, itemId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Get item by userId={}", userId);
        return itemClient.getItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable @Positive Long itemId
    ) {
        log.info("Get item by id={} by userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getRecommendedItems(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam String text
    ) {
        log.info("Get items by text={} by userId={}", text, userId);
        return itemClient.getItemsByDescription(userId, text);
    }

}
