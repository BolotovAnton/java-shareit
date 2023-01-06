package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.BookerDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponseDto {

    private int id;

    private ItemShortDto item;

    private BookerDto booker;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
