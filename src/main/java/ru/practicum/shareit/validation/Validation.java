package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.ItemAvailableValidationException;
import ru.practicum.shareit.exceptions.StartTimeAndEndTimeOfBookingShouldBeInTheFutureException;
import ru.practicum.shareit.exceptions.StartTimeOfBookingIsAfterEndTimeException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class Validation {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public void validateUserId(Integer userId) throws ValidationException {
        if (userId != null && userId <= 0) {
            log.info("userId can't be negative");
            throw new ValidationException("userId can't be negative");
        }

        if (userId != null && userRepository.findById(userId).isEmpty()) {
            log.info("user with id " + userId + " not found");
            throw new ValidationException("user with id " + userId + " not found");
        }
    }

    public void validateUserIdForItem(Integer userId, Integer itemId) throws ValidationException {
        if (itemRepository.findById(itemId).isPresent() &&
                itemRepository.findById(itemId).get().getOwnerId() != userId) {
            log.info("user with id " + userId + " can't update item with id " + itemId);
            throw new ValidationException("user with id " + userId + " can't update item with id " + itemId);
        }
    }

    public void validateItemId(Integer itemId) throws ValidationException {
        if (itemId != null && itemId <= 0) {
            log.info("itemId can't be negative");
            throw new ValidationException("itemId can't be negative");
        }
        if (itemId != null && itemRepository.findById(itemId).isEmpty()) {
            log.info("item with id " + itemId + " not found");
            throw new ValidationException("item with id " + itemId + " not found");
        }
    }

    public void validateItemAvailable(Integer itemId)
            throws ItemAvailableValidationException, ValidationException {
        validateItemId(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (!item.getAvailable()) {
            throw new ItemAvailableValidationException("item with id = " + itemId + " is not available now");
        }
    }

    public void validateBookingId(Integer bookingId) throws ValidationException {
        if (bookingId != null && bookingId <= 0) {
            log.info("bookingId can't be negative");
            throw new ValidationException("bookingId can't be negative");
        }
        if (bookingId != null && bookingRepository.findById(bookingId).isEmpty()) {
            log.info("booking with id " + bookingId + " not found");
            throw new ValidationException("booking with id " + bookingId + " not found");
        }
    }

    public void validateBookingDto(BookingDto bookingDto)
            throws StartTimeOfBookingIsAfterEndTimeException,
            StartTimeAndEndTimeOfBookingShouldBeInTheFutureException,
            ValidationException,
            ItemAvailableValidationException {

        validateItemId(bookingDto.getItemId());

        validateItemAvailable(bookingDto.getItemId());

        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new StartTimeAndEndTimeOfBookingShouldBeInTheFutureException("start time or end time of booking should be in the future");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new StartTimeOfBookingIsAfterEndTimeException("start time of booking should be after end time");
        }
    }
}
