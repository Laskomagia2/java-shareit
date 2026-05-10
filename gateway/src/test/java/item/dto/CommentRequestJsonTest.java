package item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.item.dto.CommentRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class CommentRequestJsonTest {

    @Autowired
    private JacksonTester<CommentRequest> json;

    @Test
    void testCommentRequestSerialization() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("Excellent item, thanks!");

        JsonContent<CommentRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Excellent item, thanks!");
    }

    @Test
    void testCommentRequestDeserialization() throws Exception {
        String content = "{\"text\":\"Very useful thing\"}";

        CommentRequest result = json.parse(content).getObject();

        assertThat(result.getText()).isEqualTo("Very useful thing");
    }
}
