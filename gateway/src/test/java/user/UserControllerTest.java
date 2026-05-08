package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.user.UserClient;
import ru.yandex.practicum.shareit.user.UserController;
import ru.yandex.practicum.shareit.user.dto.UserCreateRequest;
import ru.yandex.practicum.shareit.user.dto.UserUpdateRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUserEmailBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Andrei");
        request.setEmail("bad-email-format");

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).postUser(any());
    }

    @Test
    void getUserByIdIdBadRequest() throws Exception {
        mvc.perform(get("/users/-1"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUserById(anyLong());
    }

    @Test
    void updateUserBadRequest() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("invalid-email");

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_whenIdIsZero_thenReturnsBadRequest() throws Exception {
        mvc.perform(delete("/users/0"))
                .andExpect(status().isBadRequest());
    }
}
