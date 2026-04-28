package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long,Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Item postItem(Long ownerId, ItemCreateRequest request) {
        Item item = ItemMapper.mapToItem(request, idIncrement(), ownerId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> getItemsByOwner(Long ownerId) {
        return items.values().stream().filter(item -> item.getOwnerId().equals(ownerId)).toList();

    }

    @Override
    public Item getItemById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Вещь с id " + id + " не найдена");
        }
    }

    @Override
    public Item updateItem(Long ownerId, Long itemId, ItemUpdateRequest request) {
        if (items.containsKey(itemId)) {
            Item oldItem = items.get(itemId);
            if (oldItem != null) {
                String updatedName = (request.getName() != null)
                        ? request.getName()
                        : oldItem.getName();

                String updatedDescription = (request.getDescription() != null)
                        ? request.getDescription()
                        : oldItem.getDescription();

                boolean updateAvailable = (request.getAvailable() != null)
                        ? request.getAvailable()
                        : oldItem.isAvailable();

                Item updatedItem = Item.builder()
                        .id(itemId)
                        .name(updatedName)
                        .description(updatedDescription)
                        .available(updateAvailable)
                        .ownerId(oldItem.getOwnerId())
                        .build();

                items.put(itemId, updatedItem);

                return updatedItem;
            } else {
                throw new NotFoundException("Пользователь с id " + ownerId + " не является владельцем вещи");
            }
        } else {
            throw new NotFoundException("Вещь с id " + itemId + " не найдена");
        }
    }

    @Override
    public Collection<Item> getItemsByDescription(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String query = text.toLowerCase();
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(query)
                        || item.getDescription().toLowerCase().contains(query))
                .toList();
    }

    private Long idIncrement() {
        return idGenerator.incrementAndGet();
    }
}
