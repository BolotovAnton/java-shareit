package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.debug("Error {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ItemAvailableValidationException e) {
        log.debug("Error {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateKeyException(DuplicateKeyException e) {
        log.debug("Error {}", e.getMessage());
        return new ErrorResponse("Unique index or primary key violation");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStartTimeOfBookingIsAfterEndTimeException(StartTimeOfBookingIsAfterEndTimeException e) {
        log.debug("Error {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStartTimeAndEndTimeOfBookingShouldBeInTheFutureException
            (StartTimeAndEndTimeOfBookingShouldBeInTheFutureException e) {
        log.debug("Error {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingStatusException(BookingStatusException e) {
        log.debug("Error {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserDidNotBookTheItemException(CommentException e) {
        log.debug("Error {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        final String error = "Unknown " + e.getName() + ": " + e.getValue();
        log.debug(error);
        return new ErrorResponse(error);
    }
}
