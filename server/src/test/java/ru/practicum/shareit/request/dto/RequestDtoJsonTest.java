package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void requestDtoTest() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("request description")
                .build();

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("request description");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"description\":\"request description\"}";

        ItemRequestDto result = json.parse(content).getObject();

        assertThat(result.getDescription()).isEqualTo("request description");
    }
}
