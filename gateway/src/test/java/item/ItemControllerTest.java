package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.item.ItemClient;
import ru.yandex.practicum.shareit.item.ItemController;
import ru.yandex.practicum.shareit.item.dto.CommentRequest;
import ru.yandex.practicum.shareit.item.dto.ItemCreateRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final String header = "X-Sharer-User-Id";

    @Test
    void postItemBadRequest() throws Exception {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("");
        request.setDescription("Description");
        request.setAvailable(true);

        mvc.perform(post("/items")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postItem(anyLong(), any());
    }

    @Test
    void postCommentWithEmptyTextBadRequest() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("");

        mvc.perform(post("/items/1/comment")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postComment(anyLong(), anyLong(), any());
    }

    @Test
    void getItemByIdBadRequest() throws Exception {
        mvc.perform(get("/items/-1")
                        .header(header, 1L))
                .andExpect(status().isBadRequest());
    }
}
