package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestServiceImpl itemRequestService;

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto postItemRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody ItemRequestCreateRequest request
    ) {
        return itemRequestService.postItemRequest(userId, request);
    }

    @GetMapping
    public Collection<ItemRequestWithItemsDto> getItemRequestForUser(
            @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        return itemRequestService.getItemRequestForUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestWithItemsDto> getItemRequests(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return itemRequestService.getAllOtherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId
    ) {
        return itemRequestService.getRequestById(requestId);
    }

}
