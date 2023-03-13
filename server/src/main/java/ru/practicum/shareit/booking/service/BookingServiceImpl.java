package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingStatusException;
import ru.practicum.shareit.exceptions.ItemAvailableValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.Validation;

import java.time.LocalDateTime;
import java.util.List;

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
        validation.validateUser(userId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ValidationException(
                "item with id " + bookingDto.getItemId() + " not found"
        ));
        if (!item.getAvailable()) {
            throw new ItemAvailableValidationException("item with id = " + bookingDto.getItemId() + " is not available now");
        }
        Booking booking = bookingMapper.mapToBooking(bookingDto, userId);
        if (booking.getItem().getOwnerId() == userId) {
            log.info("user with id = " + userId + "can't book his own item");
            throw new ValidationException("user with id = " + userId + " can't book his own item");
        }
        Booking savedBooking = bookingRepository.save(booking);
        log.info("booking has been added");
        return bookingMapper.mapToBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Integer userId, Integer bookingId, boolean approved) {
        validation.validateUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ValidationException(
                "booking with id " + bookingId + " not found"
        ));
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.info("it's impossible to change status of booking after approved");
            throw new BookingStatusException("it's impossible to change status of booking after approved");
        }
        int ownerOfItemId = booking.getItem().getOwnerId();
        if (ownerOfItemId != userId) {
            log.info("user with id = " + userId + " is not the owner for item with id = " + ownerOfItemId);
            throw new ValidationException(
                    "user with id = " + userId + " is not the owner for item with id = " + ownerOfItemId
            );
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking approvedBooking = bookingRepository.save(booking);
        return bookingMapper.mapToBookingResponseDto(approvedBooking);
    }

    @Override
    public BookingResponseDto findBookingById(Integer userId, Integer bookingId) {
        validation.validateUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ValidationException(
                "booking with id " + bookingId + " not found"
        ));
        int ownerOfItemId = itemRepository.findById(booking.getItem().getId()).orElseThrow().getOwnerId();
        int authorOfBookingId = booking.getBooker().getId();
        if (ownerOfItemId == userId || authorOfBookingId == userId) {
            return bookingMapper.mapToBookingResponseDto(booking);
        } else {
            log.info("user with id " + userId + " does not have enough rights to view booking with id = " + bookingId);
            throw new ValidationException(
                    "user with id = " + userId + " does not have enough rights to view booking with id = " + bookingId
            );
        }
    }

    @Override
    public List<BookingResponseDto> findAllBookingsForCurrentUser(
            Integer bookerId,
            BookingState state,
            PageRequest pageRequest
    ) {
        validation.validateUser(bookerId);
        List<Booking> bookingList = getAllBookingsForCurrentUserWithDependenceOfState(
                bookerId,
                state,
                pageRequest
        ).toList();
        return bookingMapper.mapToBookingResponseDto(bookingList);
    }

    @Override
    public List<BookingResponseDto> findAllBookingsForOwnerOfItems(
            Integer ownerId,
            BookingState state,
            PageRequest pageRequest
    ) {
        validation.validateUser(ownerId);
        List<Booking> bookingList = getAllBookingsForItemsOfOwnerWithDependenceOfState(
                ownerId,
                state,
                pageRequest
        ).toList();
        if (bookingList.isEmpty()) {
            log.info("owner with id = " + ownerId + " doesn't have any items");
            throw new ValidationException("owner with id = " + ownerId + " doesn't have any items");
        }
        return bookingMapper.mapToBookingResponseDto(bookingList);
    }

    private Page<Booking> getAllBookingsForCurrentUserWithDependenceOfState(Integer bookerId,
                                                                            BookingState state,
                                                                            PageRequest pageRequest) {
        switch (state) {
            case WAITING:
                return bookingRepository
                        .getBookingsByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING, pageRequest);
            case REJECTED:
                return bookingRepository
                        .getBookingsByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED, pageRequest);
            case FUTURE:
                return bookingRepository
                        .getBookingsByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now(), pageRequest);
            case PAST:
                return bookingRepository
                        .getBookingsByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), pageRequest);
            case CURRENT:
                return bookingRepository
                        .getBookingsByBookerIdAndCurrentOrderByStartDesc(bookerId, pageRequest);
            default:
                return bookingRepository
                        .getBookingsByBookerIdOrderByStartDesc(bookerId, pageRequest);
        }
    }

    private Page<Booking> getAllBookingsForItemsOfOwnerWithDependenceOfState(Integer ownerId,
                                                                             BookingState state,
                                                                             PageRequest pageRequest) {
        switch (state) {
            case WAITING:
                return bookingRepository
                        .getBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageRequest);
            case REJECTED:
                return bookingRepository
                        .getBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageRequest);
            case FUTURE:
                return bookingRepository
                        .getBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageRequest);
            case PAST:
                return bookingRepository
                        .getBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageRequest);
            case CURRENT:
                return bookingRepository
                        .getBookingsByItem_OwnerIdAndCurrentOrderByStartDesc(ownerId, pageRequest);
            default:
                return bookingRepository
                        .getBookingsByItem_OwnerIdOrderByStartDesc(ownerId, pageRequest);
        }
    }
}
