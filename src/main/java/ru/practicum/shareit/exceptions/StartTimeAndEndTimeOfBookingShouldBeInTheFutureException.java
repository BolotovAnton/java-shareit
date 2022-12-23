package ru.practicum.shareit.exceptions;

public class StartTimeAndEndTimeOfBookingShouldBeInTheFutureException extends Exception {
    public StartTimeAndEndTimeOfBookingShouldBeInTheFutureException(String message) {
        super(message);
    }
}
