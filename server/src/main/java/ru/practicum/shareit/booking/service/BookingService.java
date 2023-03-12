package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto addBooking(Integer userId, BookingDto bookingDto);

    BookingResponseDto approveBooking(Integer userId, Integer bookingId, boolean approve);

    BookingResponseDto findBookingById(Integer userId, Integer bookingId);

    List<BookingResponseDto> findAllBookingsForCurrentUser(Integer bookerId, BookingState bookingState, PageRequest pageRequest);

    List<BookingResponseDto> findAllBookingsForOwnerOfItems(Integer ownerId, BookingState state, PageRequest pageRequest);
}
