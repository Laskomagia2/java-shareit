package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.profiles.active=test"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager em;

    @Test
    void postUpdateAndGetBooking() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("user@mail.ru");
        userCreateRequest.setName("Oleg");
        UserDto userDto = userService.postUser(userCreateRequest);

        UserCreateRequest user2CreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("user2@mail.ru");
        userCreateRequest.setName("NeOleg");
        UserDto user2Dto = userService.postUser(userCreateRequest);

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
        itemCreateRequest.setName("Item");
        itemCreateRequest.setDescription("Item description");
        itemCreateRequest.setAvailable(true);
        ItemDto itemDto = itemService.postItem(userDto.getId(), itemCreateRequest);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest();
        bookingCreateRequest.setItemId(itemDto.getId());
        bookingCreateRequest.setStart(LocalDateTime.now());
        bookingCreateRequest.setEnd(LocalDateTime.now());

        BookingDto bookingDto = bookingService.postBooking(user2Dto.getId(), bookingCreateRequest);
        bookingService.updateBooking(bookingDto.getId(), userDto.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDto.getId()).getSingleResult();

        assertThat(booking.getId(), equalTo(bookingDto.getId()));
        assertThat(booking.getBooker().getId(), equalTo(user2Dto.getId()));
        assertThat(booking.getItem().getId(), equalTo(itemDto.getId()));
    }
}
