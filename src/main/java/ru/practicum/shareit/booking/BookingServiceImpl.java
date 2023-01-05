package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.validation.Validation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final Validation validation;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingResponseDto addBooking(Integer userId, BookingDto bookingDto) {
        validation.validateUserId(userId);
        validation.validateBookingDto(bookingDto);
        Booking booking = bookingMapper.mapToBooking(bookingDto, userId);
        if (booking.getItem().getOwnerId() == userId) {
            log.info("user with id = " + userId + "can't book his own item");
            throw new ValidationException("user with id = " + userId + "can't book his own item");
        }
        bookingRepository.save(booking);
        log.info("booking has been added");
        return bookingMapper.mapToBookingResponseDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Integer userId, Integer bookingId, boolean approved) {
        validation.validateUserId(userId);
        validation.validateBookingId(bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.info("it's impossible to change status of booking after approved");
            throw new BookingStatusException("it's impossible to change status of booking after approved");
        }
        int ownerOfItemId = booking.getItem().getOwnerId();
        if (ownerOfItemId != userId) {
            log.info("user with id = " + userId + " is not the owner for item with id = " + ownerOfItemId);
            throw new ValidationException("user with id = " + userId + " is not the owner for item with id = " + ownerOfItemId);
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return bookingMapper.mapToBookingResponseDto(bookingRepository.findById(bookingId).orElseThrow());
    }

    @Override
    public BookingResponseDto findBookingById(Integer userId, Integer bookingId) {
        validation.validateUserId(userId);
        validation.validateBookingId(bookingId);
        int ownerOfItemId = itemRepository.findById(bookingRepository.findById(bookingId).orElseThrow().getItem().getId()).orElseThrow().getOwnerId();
        int authorOfBookingId = bookingRepository.findById(bookingId).orElseThrow().getBooker().getId();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (ownerOfItemId == userId || authorOfBookingId == userId) {
            return bookingMapper.mapToBookingResponseDto(booking);
        } else {
            log.info("user with id " + userId + " does not have enough rights to view booking with id = " + bookingId);
            throw new ValidationException("user with id " + userId + " does not have enough rights to view booking with id = " + bookingId);
        }
    }

    @Override
    public List<BookingResponseDto> findAllBookingsForCurrentUser(Integer bookerId, BookingState state) {
        validation.validateUserId(bookerId);
        List<Booking> bookingList = getAllBookingsWithDependenceOfState(state).stream().filter(booking -> Objects.equals(booking.getBooker().getId(), bookerId)).collect(Collectors.toList());
        return bookingMapper.mapToBookingResponseDto(bookingList);
    }

    @Override
    public List<BookingResponseDto> findAllBookingsForItemsOfOwner(Integer ownerId, BookingState state) {
        validation.validateUserId(ownerId);
        List<Booking> bookingList = getAllBookingsWithDependenceOfState(state).stream().filter(booking -> booking.getItem().getOwnerId() == ownerId).collect(Collectors.toList());
        if (bookingList.isEmpty()) {
            log.info("owner with id = " + ownerId + " doesn't have any items");
            throw new ValidationException("owner with id = " + ownerId + " doesn't have any items");
        }
        return bookingMapper.mapToBookingResponseDto(bookingList);
    }

    private List<Booking> getAllBookingsWithDependenceOfState(BookingState state) {
        switch (state) {
            case WAITING:
                return bookingRepository.getBookingsByStatusOrderByStartDesc(BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.getBookingsByStatusOrderByStartDesc(BookingStatus.REJECTED);
            case FUTURE:
                return bookingRepository.getBookingsByStartAfterOrderByStartDesc(LocalDateTime.now());
            case PAST:
                return bookingRepository.getBookingsByEndBeforeOrderByStartDesc(LocalDateTime.now());
            case CURRENT:
                return bookingRepository.getBookingsByCurrentOrderByStartDesc();
            default:
                return bookingRepository.getBookingsByOrderByStartDesc();
        }
    }
}
