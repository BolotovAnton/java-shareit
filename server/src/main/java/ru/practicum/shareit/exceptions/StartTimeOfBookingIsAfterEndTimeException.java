package ru.practicum.shareit.exceptions;

public class StartTimeOfBookingIsAfterEndTimeException extends RuntimeException {
    public StartTimeOfBookingIsAfterEndTimeException(String message) {
        super(message);
    }
}
