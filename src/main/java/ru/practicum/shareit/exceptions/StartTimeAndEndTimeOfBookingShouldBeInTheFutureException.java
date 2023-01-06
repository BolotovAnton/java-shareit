package ru.practicum.shareit.exceptions;

public class StartTimeAndEndTimeOfBookingShouldBeInTheFutureException extends RuntimeException {
    public StartTimeAndEndTimeOfBookingShouldBeInTheFutureException(String message) {
        super(message);
    }
}
