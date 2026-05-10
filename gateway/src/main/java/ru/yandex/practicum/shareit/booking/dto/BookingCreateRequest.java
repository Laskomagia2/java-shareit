package ru.yandex.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateRequest {
    @NotNull(message = "Поле id предмета не может быть пустым")
    private Long itemId;

    @FutureOrPresent
    @NotNull(message = "Поле дата начала аренды не может быть пустым")
    private LocalDateTime start;

    @Future
    @NotNull(message = "Поле дата окончания аренды не может быть пустым")
    private LocalDateTime end;
}
