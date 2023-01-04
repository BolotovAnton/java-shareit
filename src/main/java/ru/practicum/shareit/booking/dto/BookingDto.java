package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {

    private int id;

    private int itemId;

    private int bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
