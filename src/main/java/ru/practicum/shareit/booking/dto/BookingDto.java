package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {

    @Positive
    private Integer id;

    private int itemId;

    private int bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
