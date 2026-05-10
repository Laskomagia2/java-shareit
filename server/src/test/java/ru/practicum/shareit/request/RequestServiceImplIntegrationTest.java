package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.profiles.active=test"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestServiceImpl requestService;
    private final EntityManager em;

    @Test
    void createAndGetRequest() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("user@mail.ru");
        userCreateRequest.setName("Oleg");
        UserDto userDto = userService.postUser(userCreateRequest);

        UserCreateRequest user2CreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("user2@mail.ru");
        userCreateRequest.setName("NeOleg");
        UserDto user2Dto = userService.postUser(userCreateRequest);

        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("description");

        ItemRequestDto itemRequest = requestService.postItemRequest(user2Dto.getId(), request);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest completeRequest = query.setParameter("id", itemRequest.getId()).getSingleResult();

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
        itemCreateRequest.setName("Item");
        itemCreateRequest.setDescription("Item description");
        itemCreateRequest.setRequestId(itemRequest.getId());
        itemCreateRequest.setAvailable(true);
        ItemDto itemDto = itemService.postItem(userDto.getId(), itemCreateRequest);

        assertThat(completeRequest.getId(), equalTo(itemRequest.getId()));
        assertThat(completeRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(completeRequest.getId(), equalTo(itemDto.getRequestId()));
    }
}
