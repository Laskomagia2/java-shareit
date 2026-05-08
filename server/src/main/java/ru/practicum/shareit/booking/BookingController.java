package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto postBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody BookingCreateRequest request
    ) {
        return bookingService.postBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getBookingsForBooker(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.findBookingsForBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsForOwner(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.findBookingsForItemsOwner(userId, state);
    }

}
