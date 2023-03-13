package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.TimeBookingException;

import java.time.LocalDateTime;

public class Validation {

    public static void validateBookingDto(BookingDto bookingDto) {

        if (
                bookingDto.getEnd() == null ||
                        bookingDto.getStart() == null ||
                        bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                        bookingDto.getEnd().isBefore(LocalDateTime.now()) ||
                        bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                        bookingDto.getStart().equals(bookingDto.getEnd())
        ) {
            throw new TimeBookingException(
                    "Wrong booking time"
            );
        }
    }
}
