package ru.practicum.shareit.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationTest {

    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingRepository bookingRepository;
    ItemRequestRepository itemRequestRepository;
    Validation validation;
    User user;
    Item item;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        validation = new Validation(
                userRepository,
                itemRepository,
                bookingRepository,
                itemRequestRepository
        );

        user = new User(1, "user1", "user1@email.com");
        item = new Item(1, "item1", "test item1", user.getId(), false);
        bookingDto = new BookingDto(
                1,
                1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );
    }

    @Test
    void validateUserId() {

        ValidationException validationException1 = assertThrows(
                ValidationException.class,
                () -> validation.validateUserId(0)
        );

        ValidationException validationException2 = assertThrows(
                ValidationException.class,
                () -> validation.validateUserId(99)
        );

        assertEquals("userId can't be negative", validationException1.getMessage());
        assertEquals("user with id 99 not found", validationException2.getMessage());
    }

    @Test
    void validateUserIdForItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> validation.validateUserIdForItem(99, 99)
        );

        assertEquals("user with id 99 can't update item with id 99", validationException.getMessage());
    }

    @Test
    void validateItemId() {

        ValidationException validationException1 = assertThrows(
                ValidationException.class,
                () -> validation.validateItemId(0)
        );

        ValidationException validationException2 = assertThrows(
                ValidationException.class,
                () -> validation.validateItemId(99)
        );

        assertEquals("itemId can't be negative", validationException1.getMessage());
        assertEquals("item with id 99 not found", validationException2.getMessage());
    }

    @Test
    void validateItemAvailable() {

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        ItemAvailableValidationException validationException = assertThrows(
                ItemAvailableValidationException.class,
                () -> validation.validateItemAvailable(item.getId())
        );

        assertEquals("item with id = 1 is not available now", validationException.getMessage());
    }

    @Test
    void validateBookingId() {

        ValidationException validationException1 = assertThrows(
                ValidationException.class,
                () -> validation.validateBookingId(0)
        );

        ValidationException validationException2 = assertThrows(
                ValidationException.class,
                () -> validation.validateBookingId(99)
        );

        assertEquals("bookingId can't be negative", validationException1.getMessage());
        assertEquals("booking with id 99 not found", validationException2.getMessage());
    }

    @Test
    void validateBookingDto_whenStartInPast() {

        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        item.setAvailable(true);
        itemRepository.save(item);
        bookingDto.setStart(LocalDateTime.now().minusHours(1));


        StartTimeAndEndTimeOfBookingShouldBeInTheFutureException exception = assertThrows(
                StartTimeAndEndTimeOfBookingShouldBeInTheFutureException.class,
                () -> validation.validateBookingDto(bookingDto)
        );

        assertEquals("start time or end time of booking should be in the future", exception.getMessage());
    }

    @Test
    void validateBookingDto_whenEndInPast() {

        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        item.setAvailable(true);
        itemRepository.save(item);
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));

        StartTimeAndEndTimeOfBookingShouldBeInTheFutureException exception = assertThrows(
                StartTimeAndEndTimeOfBookingShouldBeInTheFutureException.class,
                () -> validation.validateBookingDto(bookingDto)
        );

        assertEquals("start time or end time of booking should be in the future", exception.getMessage());
    }

    @Test
    void validateBookingDto_whenStartIsAfterEnd() {

        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        item.setAvailable(true);
        itemRepository.save(item);
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setStart(LocalDateTime.now().plusHours(2));

        StartTimeOfBookingIsAfterEndTimeException exception = assertThrows(
                StartTimeOfBookingIsAfterEndTimeException.class,
                () -> validation.validateBookingDto(bookingDto)
        );

        assertEquals("start time of booking should be after end time", exception.getMessage());
    }

    @Test
    void validateRequestId() {

        ValidationException validationException1 = assertThrows(
                ValidationException.class,
                () -> validation.validateRequestId(0)
        );

        ValidationException validationException2 = assertThrows(
                ValidationException.class,
                () -> validation.validateRequestId(99)
        );

        assertEquals("requestId can't be negative", validationException1.getMessage());
        assertEquals("request with id 99 not found", validationException2.getMessage());
    }
}