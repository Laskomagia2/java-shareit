package user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.user.dto.UserUpdateRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class UserUpdateRequestJsonTest {

    @Autowired
    private JacksonTester<UserUpdateRequest> json;

    @Test
    void testUserUpdateRequestSerialization() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Updated Andrei");
        request.setEmail("new-email@mail.ru");

        JsonContent<UserUpdateRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Updated Andrei");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("new-email@mail.ru");
    }

    @Test
    void testUserUpdateRequestDeserialization() throws Exception {
        String content = "{\"email\":\"update@test.com\"}";

        UserUpdateRequest result = json.parse(content).getObject();

        assertThat(result.getEmail()).isEqualTo("update@test.com");
        assertThat(result.getName()).isNull();
    }

    @Test
    void testUserUpdateRequestFullDeserialization() throws Exception {
        String content = "{\"name\":\"Ivan\", \"email\":\"ivan@mail.ru\"}";

        UserUpdateRequest result = json.parse(content).getObject();

        assertThat(result.getName()).isEqualTo("Ivan");
        assertThat(result.getEmail()).isEqualTo("ivan@mail.ru");
    }
}
