package ru.practicum.shareit.item;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.profiles.active=test"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final EntityManager em;

    @Test
    void createItem() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("user@mail.ru");
        userCreateRequest.setName("Oleg");
        UserDto userDto = userService.postUser(userCreateRequest);

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
        itemCreateRequest.setName("Item");
        itemCreateRequest.setDescription("Item description");
        itemCreateRequest.setAvailable(true);
        ItemDto itemDto = itemService.postItem(userDto.getId(), itemCreateRequest);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(userDto.getId()));
    }

    @Test
    void createItemAddCommentAndGet() {
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
        bookingCreateRequest.setStart(LocalDateTime.now().minusDays(2));
        bookingCreateRequest.setEnd(LocalDateTime.now().minusDays(1));

        BookingDto bookingDto = bookingService.postBooking(user2Dto.getId(), bookingCreateRequest);
        bookingService.updateBooking(bookingDto.getId(), userDto.getId(), true);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDto.getId()));
        assertThat(item.getName(), equalTo(itemCreateRequest.getName()));
        assertThat(item.getOwner().getId(), equalTo(userDto.getId()));
        assertThat(item.getAvailable(), equalTo(itemCreateRequest.getAvailable()));

        CommentRequest comment = new CommentRequest();
        comment.setText("4tka");
        CommentDto createdComment = itemService.postComment(user2Dto.getId(), itemDto.getId(), comment);

        TypedQuery<Comment> query2 = em.createQuery("Select c from Comment c where c.author.id = :id", Comment.class);
        Comment complComment = query2.setParameter("id", user2Dto.getId()).getSingleResult();

        assertThat(complComment.getId(), equalTo(createdComment.getId()));
        assertThat(complComment.getText(), equalTo(comment.getText()));
        assertThat(complComment.getItem().getId(), equalTo(itemDto.getId()));
        assertThat(complComment.getAuthor().getId(), equalTo(user2Dto.getId()));

    }

}
