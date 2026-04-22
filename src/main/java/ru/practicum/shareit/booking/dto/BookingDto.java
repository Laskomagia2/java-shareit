package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private final Long bookerId;
    private final Status status;
}
