package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemDto postItem(Long ownerId, ItemCreateRequest request) {
        log.debug("Запрос на создание вещи пользователем {}", ownerId);
        userService.getUserById(ownerId);
        Item item = itemStorage.postItem(ownerId, request);
        log.debug("Вещь с id {} создана пользователем {}", item.getId(), ownerId);
        return ItemMapper.mapToItemDto(item);
    }

    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        log.debug("Запрос на получение вещей пользователя {}", ownerId);
        userService.getUserById(ownerId);
        return itemStorage.getItemsByOwner(ownerId).stream().map(ItemMapper::mapToItemDto).toList();
    }

    public ItemDto getItemById(Long id) {
        log.debug("Запрос на получение вещи с id {}", id);
        return ItemMapper.mapToItemDto(itemStorage.getItemById(id));
    }

    public Collection<ItemDto> getItemsByDescription(String desc) {
        log.debug("Запрос на получение вещи с описанием {}", desc);
        return itemStorage.getItemsByDescription(desc).stream().map(ItemMapper::mapToItemDto).toList();
    }

    public ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateRequest newItem) {
        log.debug("Запрос на обновление вещи с id: {}, пользователем {}", itemId, ownerId);
        userService.getUserById(ownerId);
        return ItemMapper.mapToItemDto(itemStorage.updateItem(ownerId, itemId, newItem));
    }
}
