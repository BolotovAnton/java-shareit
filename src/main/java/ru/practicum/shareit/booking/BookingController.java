package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @Valid @RequestBody BookingDto bookingDto) throws
            ValidationException,
            StartTimeOfBookingIsAfterEndTimeException,
            StartTimeAndEndTimeOfBookingShouldBeInTheFutureException,
            ItemAvailableValidationException {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId,
                                             @RequestParam boolean approved) throws
            ValidationException,
            BookingStatusException {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable Integer bookingId) throws ValidationException {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllBookingsForCurrentUser(@RequestHeader("X-Sharer-User-Id") Integer bookerId,
                                                                  @RequestParam(defaultValue = "ALL") BookingState state)
            throws ValidationException {
        return bookingService.findAllBookingsForCurrentUser(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllBookingsForOwnerOfItems(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                                   @RequestParam(defaultValue = "ALL") BookingState state)
            throws ValidationException {
        return bookingService.findAllBookingsForItemsOfOwner(ownerId, state);
    }
}
