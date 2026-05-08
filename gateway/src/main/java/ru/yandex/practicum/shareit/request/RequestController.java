package ru.yandex.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareit.request.dto.ItemRequestCreateRequest;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postItemRequest(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemRequestCreateRequest request
    ) {
        log.info("Post request {}, by userId={}",request, userId);
        return requestClient.postItemRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestForUser(
            @RequestHeader(USER_HEADER) Long userId
    ) {
        log.info("Get request for userId={}", userId);
        return requestClient.getItemRequestForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(
            @RequestHeader(USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "20") Integer size
    ) {
        log.info("Get requests with userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllOtherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long requestId
    ) {
        log.info("Get request by requestId={}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}
