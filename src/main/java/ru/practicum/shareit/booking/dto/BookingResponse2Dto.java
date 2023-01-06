package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse2Dto {

    private int id;

    private ItemShortDto item;

    private Integer bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
