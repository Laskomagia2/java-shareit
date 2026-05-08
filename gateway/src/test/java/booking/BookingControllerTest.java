package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.booking.BookingClient;
import ru.yandex.practicum.shareit.booking.BookingController;
import ru.yandex.practicum.shareit.booking.dto.BookingCreateRequest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final String header = "X-Sharer-User-Id";

    @Test
    void postBookingBadRequestTest() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().minusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void getBookingsBookingBadRequestTest() throws Exception {
        mvc.perform(get("/bookings")
                        .header(header, 1L)
                        .param("state", "UNKNOWN_STATE"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getBookingsBookingBadParamFromRequestTest() throws Exception {
        mvc.perform(get("/bookings")
                        .header(header, 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsBookingBadPramSizeRequestTest() throws Exception {
        mvc.perform(get("/bookings")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

}
