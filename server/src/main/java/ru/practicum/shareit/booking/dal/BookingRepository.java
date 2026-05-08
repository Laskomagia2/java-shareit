package ru.practicum.shareit.booking.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId, LocalDateTime start, LocalDateTime end
    );

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(
            Long userId, LocalDateTime end
    );

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            Long userId, LocalDateTime start
    );

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    Collection<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime start, LocalDateTime end
    );

    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long ownerId, LocalDateTime end
    );

    Collection<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long ownerId, LocalDateTime start
    );

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, Status status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            Long itemId, Status status, LocalDateTime now
    );

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, Status status, LocalDateTime now
    );

}
