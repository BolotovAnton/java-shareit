package ru.practicum.shareit.exceptions;

public class UserDidNotBookTheItemException extends RuntimeException {
    public UserDidNotBookTheItemException(String message) {
        super(message);
    }
}
