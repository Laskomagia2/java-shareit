package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@UtilityClass
public class BookingMapper {
    public Booking mapToBook(BookingCreateRequest request) {
        return Booking.builder()
                .start(request.getStart())
                .end(request.getEnd())
                .build();
    }

    public BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new BookingDto.BookerDto(booking.getBooker().getId()))
                .item(new BookingDto.ItemDto(booking.getItem().getId(), booking.getItem().getName()))
                .build();
    }
}
