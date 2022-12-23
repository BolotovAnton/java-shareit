package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ItemAvailableValidationException;
import ru.practicum.shareit.exceptions.StartTimeAndEndTimeOfBookingShouldBeInTheFutureException;
import ru.practicum.shareit.exceptions.StartTimeOfBookingIsAfterEndTimeException;
import ru.practicum.shareit.exceptions.ValidationException;

public interface BookingService {
    BookingResponseDto addBooking(Integer userId, BookingDto bookingDto) throws ValidationException, ItemAvailableValidationException, StartTimeOfBookingIsAfterEndTimeException, StartTimeAndEndTimeOfBookingShouldBeInTheFutureException;

    BookingResponseDto approveBooking(Integer userId, Integer bookingId, boolean approve) throws Exception;

    BookingResponseDto findBookingById(Integer userId, Integer bookingId) throws ValidationException;
}
