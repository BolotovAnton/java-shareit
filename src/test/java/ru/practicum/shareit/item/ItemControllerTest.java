package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    CommentDto commentDto;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {

        commentDto = new CommentDto(
                1,
                "comment",
                "author",
                LocalDateTime.now()
        );

        itemDto = new ItemDto(
                1,
                "item1",
                "itemtest1",
                true,
                new BookingShortDto(1, 1),
                new BookingShortDto(2, 1),
                Set.of(commentDto),
                1
        );
    }

    @Test
    void addItem() throws Exception {

        when(itemService.addItem(anyInt(), any())).thenReturn(itemDto);
        String body = mapper.writeValueAsString(itemDto);

        mockMvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(body));

        verify(itemService, times(1)).addItem(anyInt(), any());
    }

    @Test
    void updateUser() throws Exception {

        when(itemService.updateItem(anyInt(), anyInt(), any())).thenReturn(itemDto);
        String body = mapper.writeValueAsString(itemDto);

        mockMvc.perform(
                        patch("/items/1")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isOk());

        verify(itemService, times(1)).updateItem(anyInt(), anyInt(), any());
    }

    @Test
    void findItemById() throws Exception {

        when(itemService.findItemById(anyInt(), any())).thenReturn(itemDto);
        String body = mapper.writeValueAsString(itemDto);

        mockMvc.perform(
                        get("/items/1")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(body));

        verify(itemService, times(1)).findItemById(anyInt(), any());
    }

    @Test
    void findAllItemsForUser() throws Exception {

        when(itemService.findAllItemsForUser(anyInt(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1)).findAllItemsForUser(anyInt(), any());
    }

    @Test
    void searchItemByText() throws Exception {

        when(itemService.searchItemsByText(anyString(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/items/search")
                                .header("X-Sharer-User-Id", 1)
                                .queryParam("text", "searchtext")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1)).searchItemsByText(anyString(), any());
    }

    @Test
    void addComment() throws Exception {

        when(itemService.addComment(anyInt(), anyInt(), any())).thenReturn(commentDto);
        String body = mapper.writeValueAsString(commentDto);

        mockMvc.perform(
                        post("/items/1/comment")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(body));

        verify(itemService, times(1)).addComment(anyInt(), anyInt(), any());
    }
}