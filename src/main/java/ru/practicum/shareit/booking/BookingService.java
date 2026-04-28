package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingStorage;
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;

    private enum SearchingCriteria {
        ALL,
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED,
        CURRENT,
        PAST,
        FUTURE;
    }

    @Transactional
    public BookingDto postBooking(Long userId, BookingCreateRequest request) {
        Item item = itemStorage.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с id " + request.getItemId() + " не найден"));
        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new ValidationBadRequestException("Вещь недоступна для бронирования");
        }

        if (request.getEnd().isBefore(request.getStart()) || request.getEnd().equals(request.getStart())) {
            throw new ValidationBadRequestException("Неверное время бронирования");
        }

        Booking booking = Booking.builder()
                .start(request.getStart())
                .end(request.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        return BookingMapper.mapToBookingDto(bookingStorage.save(booking));
    }

    @Transactional
    public BookingDto updateBooking(Long bookingId, Long ownerId, Boolean approve) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id " + bookingId + " не найдена"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NoPermissionException("Пользователь с id " + ownerId + " не является владельцем вещи");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationBadRequestException("Статус аренды уже изменен");
        }

        if (approve) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    public BookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id " + bookingId + " не найдена"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не является владельцем или букером");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    public Collection<BookingDto> findBookingsForBooker(Long userId, String req) {
        validateUser(userId);

        SearchingCriteria criteria = parseState(req);
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> result = switch (criteria) {
            case ALL -> bookingStorage.findAllByBookerIdOrderByStartDesc(userId);
            case WAITING -> bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case APPROVED -> bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.APPROVED);
            case REJECTED -> bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            case CANCELED -> bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.CANCELED);
            case CURRENT -> bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingStorage.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
        };
        return result.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    public Collection<BookingDto> findBookingsForItemsOwner(Long owner, String req) {
        validateUser(owner);

        SearchingCriteria criteria = parseState(req);
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> result = switch (criteria) {
            case ALL -> bookingStorage.findAllByItemOwnerIdOrderByStartDesc(owner);
            case WAITING -> bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner, Status.WAITING);
            case APPROVED -> bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner, Status.APPROVED);
            case REJECTED -> bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner, Status.REJECTED);
            case CANCELED -> bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner, Status.CANCELED);
            case CURRENT -> bookingStorage.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(owner, now, now);
            case PAST -> bookingStorage.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(owner, now);
            case FUTURE -> bookingStorage.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(owner, now);
        };
        return result.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    private void validateUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private SearchingCriteria parseState(String state) {
        try {
            return SearchingCriteria.valueOf(state.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

}
