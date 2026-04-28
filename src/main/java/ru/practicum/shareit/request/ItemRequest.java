package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    private final Long id;
    private final String description;
    private final Long requestor;
    private final LocalDateTime created;
}
