package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.Collection;

@Data
@Builder
public class ItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Collection<CommentDto> comments;
    private Long requestId;
    private Long ownerId;

    @Data
    @AllArgsConstructor
    public static class BookingShortDto {
        private Long id;
        private Long bookerId;
    }
}
