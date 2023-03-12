package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.ItemAvailableValidationException;
import ru.practicum.shareit.exceptions.StartTimeAndEndTimeOfBookingShouldBeInTheFutureException;
import ru.practicum.shareit.exceptions.StartTimeOfBookingIsAfterEndTimeException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class Validation {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    public void validateUserId(Integer userId) {
        if (userId != null && userId <= 0) {
            log.info("userId can't be negative");
            throw new ValidationException("userId can't be negative");
        }

        if (userId != null && userRepository.findById(userId).isEmpty()) {
            log.info("user with id " + userId + " not found");
            throw new ValidationException("user with id " + userId + " not found");
        }
    }

    public void validateUserIdForItem(Integer userId, Integer itemId) {
        if (itemRepository.findById(itemId).orElseThrow().getOwnerId() != userId) {
            log.info("user with id " + userId + " can't update item with id " + itemId);
            throw new ValidationException("user with id " + userId + " can't update item with id " + itemId);
        }
    }

    public void validateItemId(Integer itemId) {
        if (itemId != null && itemId <= 0) {
            log.info("itemId can't be negative");
            throw new ValidationException("itemId can't be negative");
        }
        if (itemId != null && itemRepository.findById(itemId).isEmpty()) {
            log.info("item with id " + itemId + " not found");
            throw new ValidationException("item with id " + itemId + " not found");
        }
    }

    public void validateItemAvailable(Integer itemId) {
        validateItemId(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (!item.getAvailable()) {
            throw new ItemAvailableValidationException("item with id = " + itemId + " is not available now");
        }
    }

    public void validateBookingId(Integer bookingId) {
        if (bookingId != null && bookingId <= 0) {
            log.info("bookingId can't be negative");
            throw new ValidationException("bookingId can't be negative");
        }
        if (bookingId != null && bookingRepository.findById(bookingId).isEmpty()) {
            log.info("booking with id " + bookingId + " not found");
            throw new ValidationException("booking with id " + bookingId + " not found");
        }
    }

    public void validateBookingDto(BookingDto bookingDto) {

        validateItemId(bookingDto.getItemId());

        validateItemAvailable(bookingDto.getItemId());

        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new StartTimeAndEndTimeOfBookingShouldBeInTheFutureException(
                    "start time or end time of booking should be in the future"
            );
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new StartTimeOfBookingIsAfterEndTimeException("start time of booking should be after end time");
        }
    }

    public void validateRequestId(Integer requestId) {
        if (requestId != null && requestId <= 0) {
            log.info("requestId can't be negative");
            throw new ValidationException("requestId can't be negative");
        }
        if (requestId != null && itemRequestRepository.findById(requestId).isEmpty()) {
            log.info("request with id " + requestId + " not found");
            throw new ValidationException("request with id " + requestId + " not found");
        }
    }
}
