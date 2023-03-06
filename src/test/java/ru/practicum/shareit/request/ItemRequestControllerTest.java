package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    ItemRequestDto itemRequestDto;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {

        itemDto = new ItemDto(
                1,
                "item1",
                "itemtest1",
                true,
                new BookingShortDto(1, 1),
                new BookingShortDto(2, 1),
                null,
                1
        );

        itemRequestDto = new ItemRequestDto(
                1,
                "itemrequest",
                LocalDateTime.now(),
                Set.of(itemDto)
        );
    }

    @Test
    void addRequest() throws Exception {

        when(itemRequestService.addRequest(anyInt(), any())).thenReturn(itemRequestDto);
        String body = mapper.writeValueAsString(itemRequestDto);

        mockMvc.perform(
                        post("/requests")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(body));

        verify(itemRequestService, times(1)).addRequest(anyInt(), any());
    }

    @Test
    void findRequestsOfUser() throws Exception {

        when(itemRequestService.findRequestsOfUser(anyInt(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService, times(1)).findRequestsOfUser(anyInt(), any());
    }

    @Test
    void findAllRequests() throws Exception {

        when(itemRequestService.findAllRequests(anyInt(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService, times(1)).findAllRequests(anyInt(), any());
    }

    @Test
    void findRequestById() throws Exception {

        when(itemRequestService.findById(anyInt(), any())).thenReturn(itemRequestDto);
        String body = mapper.writeValueAsString(itemRequestDto);

        mockMvc.perform(
                        get("/requests/1")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(body));

        verify(itemRequestService, times(1)).findById(anyInt(), any());
    }
}