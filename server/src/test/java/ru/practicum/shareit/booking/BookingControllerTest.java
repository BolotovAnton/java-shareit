package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.BookerDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {BookingController.class})
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;
    BookingResponseDto bookingResponseDto;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingResponseDto = new BookingResponseDto(
                1,
                new ItemShortDto(1, "item"),
                new BookerDto(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING
        );

        bookingDto = new BookingDto(
                1,
                1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("MockMvc test for Add Booking")
    void addBooking_whenInvoked_thenResponseStatusIsOk() throws Exception {
        when(bookingService.addBooking(anyInt(), any())).thenReturn(bookingResponseDto);
        String body = mapper.writeValueAsString(bookingDto);

        mockMvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingResponseDto)));

        verify(bookingService, times(1)).addBooking(anyInt(), any());
    }

    @Test
    void approveBooking_whenInvoked_thenResponseStatusIsOk() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingResponseDto);

        mockMvc.perform(
                        patch("/bookings/1")
                                .header("X-Sharer-User-Id", 1)
                                .queryParam("approved", "true")
                )
                .andExpect(status().isOk());

        verify(bookingService, times(1)).approveBooking(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void findBookingById_whenBookingFound_thenStatusIsOkWithBookingResponseDtoInBody() throws Exception {
        when(bookingService.findBookingById(anyInt(), anyInt())).thenReturn(bookingResponseDto);
        String body = mapper.writeValueAsString(bookingResponseDto);

        mockMvc.perform(
                        get("/bookings/1")
                                .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(body));

        verify(bookingService, times(1)).findBookingById(anyInt(), anyInt());
    }

    @Test
    void findAllBookingsForCurrentUser_whenBookingsFound_thenStatusIsOkWithListInBody() throws Exception {
        when(bookingService.findAllBookingsForCurrentUser(anyInt(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .queryParam("state", "ALL")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1)).findAllBookingsForCurrentUser(anyInt(), any(), any());
    }

    @Test
    void findAllBookingsForOwnerOfItems_whenBookingsFound_thenStatusIsOkWithListInBody() throws Exception {
        when(bookingService.findAllBookingsForOwnerOfItems(anyInt(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1)
                                .queryParam("state", "ALL")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1)).findAllBookingsForOwnerOfItems(anyInt(), any(), any());
    }
}