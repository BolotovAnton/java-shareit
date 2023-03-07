package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.util.MyPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody BookingDto bookingDto
    ) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(
            @RequestHeader("X-Sharer-User-Id")
            Integer userId, @PathVariable Integer bookingId
    ) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllBookingsForCurrentUser(
            @RequestHeader("X-Sharer-User-Id") Integer bookerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        PageRequest pageRequest = new MyPageRequest(from, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingService.findAllBookingsForCurrentUser(bookerId, state, pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllBookingsForOwnerOfItems(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        PageRequest pageRequest = new MyPageRequest(from, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingService.findAllBookingsForOwnerOfItems(ownerId, state, pageRequest);
    }
}
