package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
