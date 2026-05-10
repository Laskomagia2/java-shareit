package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private BookingDto dto;
    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void dtoSetUp() {
        dto = BookingDto.builder()
                .id(1L)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void postBookingTest() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingService.postBooking(anyLong(), any())).thenReturn(dto);

        mvc.perform(post("/bookings")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.status", is(dto.getStatus().toString())));
    }

    @Test
    void patchBookingTest() throws Exception {
        BookingDto updateDto = BookingDto.builder()
                .id(1L)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(updateDto);

        mvc.perform(patch("/bookings/1")
                        .header(header, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong())).thenReturn(dto);

        mvc.perform(get("/bookings/1")
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())));
    }

    @Test
    void getBookingsForBookerTest() throws Exception {
        when(bookingService.findBookingsForBooker(anyLong(), anyString()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/bookings")
                        .header(header, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId().intValue())));
    }

    @Test
    void getBookingsForOwnerTest() throws Exception {
        when(bookingService.findBookingsForItemsOwner(anyLong(), anyString()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/bookings/owner")
                        .header(header, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId().intValue())));
    }


}
