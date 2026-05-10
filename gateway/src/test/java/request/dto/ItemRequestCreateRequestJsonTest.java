package request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.request.dto.ItemRequestCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemRequestCreateRequestJsonTest {

    @Autowired
    private JacksonTester<ItemRequestCreateRequest> json;

    @Test
    void testItemRequestCreateRequestSerialization() throws Exception {
        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("Нужна стремянка высотой 2 метра");

        JsonContent<ItemRequestCreateRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужна стремянка высотой 2 метра");
    }

    @Test
    void testItemRequestCreateRequestDeserialization() throws Exception {
        String content = "{\"description\":\"Ищу перфоратор на выходные\"}";

        ItemRequestCreateRequest result = json.parse(content).getObject();

        assertThat(result.getDescription()).isEqualTo("Ищу перфоратор на выходные");
    }
}
