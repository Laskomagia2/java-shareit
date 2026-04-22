package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto postItem(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemCreateRequest request
    ) {
        return itemService.postItem(userId, request);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemUpdateRequest request,
            @PathVariable @Positive Long itemId
    ) {
        return itemService.updateItem(userId, itemId, request);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable @Positive Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getRecommendedItems(@RequestParam String text) {
        return itemService.getItemsByDescription(text);
    }

}
