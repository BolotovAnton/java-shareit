package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
public class Booking {

    private int bookingId;

    private int itemId;

    private LocalDate start;

    private LocalDate end;

    private boolean confirmation;

    private String review;
}
