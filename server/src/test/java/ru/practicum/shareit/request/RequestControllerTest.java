package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto dto;
    private ItemRequestWithItemsDto withItemsDto;
    private ItemRequestCreateRequest createRequest;

    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void dtoSetUp() {
        dto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();

        withItemsDto = ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        createRequest = new ItemRequestCreateRequest();
        createRequest.setDescription("description");
    }

    @Test
    void postItemRequest() throws Exception {
        when(itemRequestService.postItemRequest(anyLong(), any()))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    @Test
    void getItemRequestForUser() throws Exception {
        when(itemRequestService.getItemRequestForUser(anyLong()))
                .thenReturn(List.of(withItemsDto));

        mvc.perform(get("/requests")
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(withItemsDto.getId().intValue())))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    void getItemRequestsAll() throws Exception {
        when(itemRequestService.getAllOtherRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(withItemsDto));

        mvc.perform(get("/requests/all")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(withItemsDto.getDescription())));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong()))
                .thenReturn(withItemsDto);

        mvc.perform(get("/requests/1")
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(withItemsDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(withItemsDto.getDescription())));
    }
}
