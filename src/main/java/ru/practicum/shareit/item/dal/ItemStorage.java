package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    public Item postItem(Long ownerId, ItemCreateRequest request);

    public Collection<Item> getItemsByOwner(Long ownerId);

    public Item getItemById(Long id);

    public Collection<Item> getItemsByDescription(String desc);

    public Item updateItem(Long ownerId, Long itemId, ItemUpdateRequest newItem);

}
