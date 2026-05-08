package booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.booking.dto.BookingCreateRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class BookingCreateRequestJsonTest {

    @Autowired
    private JacksonTester<BookingCreateRequest> json;

    @Test
    void testBookingCreateRequestSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 12, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 10, 18, 0, 0);

        BookingCreateRequest request = new BookingCreateRequest();
        request.setItemId(1L);
        request.setStart(start);
        request.setEnd(end);

        JsonContent<BookingCreateRequest> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-12-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-12-10T18:00:00");
    }

    @Test
    void testBookingCreateRequestDeserialization() throws Exception {
        String content = "{\"itemId\":10, \"start\":\"2026-11-01T12:00:00\", \"end\":\"2026-11-05T12:00:00\"}";

        BookingCreateRequest result = json.parse(content).getObject();

        assertThat(result.getItemId()).isEqualTo(10L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2026, 11, 1, 12, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2026, 11, 5, 12, 0, 0));
    }
}