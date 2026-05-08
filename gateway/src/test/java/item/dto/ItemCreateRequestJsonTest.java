package item.dto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.item.dto.ItemCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemCreateRequestJsonTest {

    @Autowired
    private JacksonTester<ItemCreateRequest> json;

    @Test
    void testItemCreateRequestSerialization() throws Exception {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Дрель");
        request.setDescription("Мощная ударная дрель");
        request.setAvailable(true);
        request.setRequestId(1L);

        JsonContent<ItemCreateRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Мощная ударная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemCreateRequestDeserialization() throws Exception {
        String content = "{" +
                "\"name\":\"Дрель\"," +
                "\"description\":\"Мощная ударная дрель\"," +
                "\"available\":true," +
                "\"requestId\":1" +
                "}";

        ItemCreateRequest result = json.parse(content).getObject();

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Мощная ударная дрель");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(1L);
    }

    @Test
    void testItemCreateRequestDeserializationWithoutRequestId() throws Exception {
        String content = "{\"name\":\"Дрель\", \"description\":\"Обычная\", \"available\":false}";

        ItemCreateRequest result = json.parse(content).getObject();

        assertThat(result.getRequestId()).isNull();
        assertThat(result.getAvailable()).isFalse();
    }
}
