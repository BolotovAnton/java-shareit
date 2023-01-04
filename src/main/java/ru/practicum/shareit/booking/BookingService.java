package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.*;

import java.util.List;

public interface BookingService {
    BookingResponseDto addBooking(Integer userId, BookingDto bookingDto) throws
            ValidationException,
            StartTimeOfBookingIsAfterEndTimeException,
            StartTimeAndEndTimeOfBookingShouldBeInTheFutureException,
            ItemAvailableValidationException;

    BookingResponseDto approveBooking(Integer userId, Integer bookingId, boolean approve) throws
            ValidationException, BookingStatusException;

    BookingResponseDto findBookingById(Integer userId, Integer bookingId) throws
            ValidationException;

    List<BookingResponseDto> findAllBookingsForCurrentUser(Integer bookerId, BookingState bookingState) throws
            ValidationException;

    List<BookingResponseDto> findAllBookingsForItemsOfOwner(Integer ownerId, BookingState state) throws
            ValidationException;
}
