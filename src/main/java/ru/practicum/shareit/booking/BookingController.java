package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody BookingDto bookingDto)
            throws Exception {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId,
                                             @RequestParam boolean approved) throws Exception {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable Integer bookingId) throws ValidationException {
        return bookingService.findBookingById(userId, bookingId);
    }
}
