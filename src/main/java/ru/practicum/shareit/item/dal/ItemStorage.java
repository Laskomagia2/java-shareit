package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item postItem(Long ownerId, ItemCreateRequest request);

    Collection<Item> getItemsByOwner(Long ownerId);

    Item getItemById(Long id);

    Collection<Item> getItemsByDescription(String desc);

    Item updateItem(Long ownerId, Long itemId, ItemUpdateRequest newItem);

}
