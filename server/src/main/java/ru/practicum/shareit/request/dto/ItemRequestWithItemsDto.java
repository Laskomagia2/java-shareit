package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemRequestWithItemsDto {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private Collection<ItemDto> items;
}
