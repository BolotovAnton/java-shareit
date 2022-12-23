package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ItemAvailableValidationException;
import ru.practicum.shareit.exceptions.StartTimeAndEndTimeOfBookingShouldBeInTheFutureException;
import ru.practicum.shareit.exceptions.StartTimeOfBookingIsAfterEndTimeException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.validation.Validation;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final Validation validation;

    @Transactional
    @Override
    public BookingResponseDto addBooking(Integer userId, BookingDto bookingDto) throws ValidationException,
            ItemAvailableValidationException, StartTimeOfBookingIsAfterEndTimeException,
            StartTimeAndEndTimeOfBookingShouldBeInTheFutureException {
        validation.validateUserId(userId);
        validation.validateBookingDto(bookingDto);
        Booking booking = BookingMapper.mapToBooking(bookingDto, userId);
        bookingRepository.save(booking);
        log.info("booking has been added");
        return BookingMapper.mapToBookingResponseDto(itemRepository, booking);
    }

    @Override
    public BookingResponseDto approveBooking(Integer userId, Integer bookingId, boolean approved) throws Exception {
        validation.validateUserId(userId);
        validation.validateBookingId(bookingId);
        int ownerOfItemId = itemRepository.findById(bookingRepository
                        .findById(bookingId)
                        .orElseThrow()
                        .getItemId()).orElseThrow().getOwnerId();
        if (ownerOfItemId != userId) {
            log.info("user with id = " + userId + " is not the owner for item with id = " + ownerOfItemId);
            throw new ValidationException("user with id = " + userId + " is not the owner for item with id = " + ownerOfItemId);
        }
        if (approved) {
            bookingRepository.findById(bookingId).orElseThrow().setStatus(BookingStatus.APPROVED);
        } else {
            bookingRepository.findById(bookingId).orElseThrow().setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.mapToBookingResponseDto(itemRepository, bookingRepository.findById(bookingId).orElseThrow());
    }

    @Override
    public BookingResponseDto findBookingById(Integer userId, Integer bookingId) throws ValidationException {
        validation.validateUserId(userId);
        validation.validateBookingId(bookingId);
        int ownerOfItemId = itemRepository.findById(bookingRepository
                .findById(bookingId)
                .orElseThrow()
                .getItemId()).orElseThrow().getOwnerId();
        int authorOfBookingId = bookingRepository.findById(bookingId).orElseThrow().getBookerId();
        if (ownerOfItemId == userId || authorOfBookingId == userId) {
            return BookingMapper.mapToBookingResponseDto(itemRepository, bookingRepository.findById(bookingId).orElseThrow());
        } else {
            log.info("user with id " + userId + " does not have enough rights to view booking with id = " + bookingId);
            throw new ValidationException("user with id " + userId + " does not have enough rights to view booking with id = " + bookingId);
        }
    }
}
