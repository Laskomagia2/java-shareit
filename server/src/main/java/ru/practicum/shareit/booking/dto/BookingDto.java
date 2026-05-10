package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Status status;
    private final BookerDto booker;
    private final ItemDto item;

    @Data
    @AllArgsConstructor
    public static class BookerDto {
        private Long id;
    }

    @Data
    @AllArgsConstructor
    public static class ItemDto {
        private Long id;
        private String name;
    }
}
