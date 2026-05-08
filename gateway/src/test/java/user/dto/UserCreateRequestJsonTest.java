package user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.shareit.ShareItGateway;
import ru.yandex.practicum.shareit.user.dto.UserCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class UserCreateRequestJsonTest {

    @Autowired
    private JacksonTester<UserCreateRequest> json;

    @Test
    void testUserCreateRequestSerialization() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Andrei");
        request.setEmail("andrei@mail.ru");

        JsonContent<UserCreateRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Andrei");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("andrei@mail.ru");
    }

    @Test
    void testUserCreateRequestDeserialization() throws Exception {
        String content = "{\"email\":\"test@example.com\", \"name\":\"Tester\"}";

        UserCreateRequest result = json.parse(content).getObject();

        assertThat(result.getName()).isEqualTo("Tester");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }
}
