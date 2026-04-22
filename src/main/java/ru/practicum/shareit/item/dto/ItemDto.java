package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final boolean available;
}
