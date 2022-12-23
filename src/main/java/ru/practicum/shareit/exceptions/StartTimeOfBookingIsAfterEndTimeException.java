package ru.practicum.shareit.exceptions;

public class StartTimeOfBookingIsAfterEndTimeException extends Exception{
    public StartTimeOfBookingIsAfterEndTimeException(String message) {
        super(message);
    }
}
