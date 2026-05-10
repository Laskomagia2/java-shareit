package request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.request.RequestClient;
import ru.yandex.practicum.shareit.request.RequestController;
import ru.yandex.practicum.shareit.request.dto.ItemRequestCreateRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final String header = "X-Sharer-User-Id";

    @Test
    void postItemRequestBadRequest() throws Exception {
        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("");

        mvc.perform(post("/requests")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).postItemRequest(anyLong(), any());
    }

    @Test
    void getItemRequestsBadRequest() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(header, 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemRequestsBadParamSizeRequest() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }
}
