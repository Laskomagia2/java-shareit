package item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.item.dto.ItemUpdateRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemUpdateRequestJsonTest {

    @Autowired
    private JacksonTester<ItemUpdateRequest> json;

    @Test
    void testItemUpdateRequestSerialization() throws Exception {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("New Name");
        request.setDescription("New Description");
        request.setAvailable(false);

        JsonContent<ItemUpdateRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("New Name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("New Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isFalse();
    }

    @Test
    void testItemUpdateRequestDeserialization() throws Exception {
        String content = "{\"name\":\"Updated Name\"}";

        ItemUpdateRequest result = json.parse(content).getObject();

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getAvailable()).isNull();
    }

    @Test
    void testItemUpdateRequestFullDeserialization() throws Exception {
        String content = "{\"name\":\"Name\", \"description\":\"Desc\", \"available\":true}";

        ItemUpdateRequest result = json.parse(content).getObject();

        assertThat(result.getName()).isEqualTo("Name");
        assertThat(result.getDescription()).isEqualTo("Desc");
        assertThat(result.getAvailable()).isTrue();
    }
}
