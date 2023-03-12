package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BookingStatusException;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    BookingService bookingService;
    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;
    Validation validation;
    BookingMapper bookingMapper;

    Booking booking1;
    Booking booking2;
    User user1;
    User user2;
    User user3;
    Item item1;
    Item item2;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {

        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        validation = mock(Validation.class);
        bookingMapper = new BookingMapper(itemRepository, userRepository);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, validation, bookingMapper);

        user1 = new User(1, "user1", "user1@email.com");
        user2 = new User(2, "user2", "user2@email.com");
        user3 = new User(3, "user3", "user3@email.com");
        item1 = new Item(1, "item1", "test item1", user1.getId(), true);
        item2 = new Item(2, "item2", "test item2", user1.getId(), true);

        booking1 = new Booking(
                1,
                item1,
                user2,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                BookingStatus.WAITING
        );

        booking2 = new Booking(
                2,
                item1,
                user3,
                LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(5),
                BookingStatus.REJECTED
        );

        bookingDto = new BookingDto(
                1,
                1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );
    }

    @Test
    void addBooking_whenBookNotOwnerOfItem_thenBookingSaved() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any())).thenReturn(booking1);

        BookingResponseDto responseDto = bookingService.addBooking(user2.getId(), bookingDto);

        assertEquals(booking1.getId(), responseDto.getId());
        assertEquals(BookingStatus.WAITING, responseDto.getStatus());
        assertEquals(booking1.getItem().getName(), responseDto.getItem().getName());
        assertEquals(booking1.getBooker().getId(), responseDto.getBooker().getId());

    }

    @Test
    void addBooking_whenBookOwnerOfItem_thenValidationExceptionThrown() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        when(bookingRepository.save(any())).thenReturn(booking1);

        ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(user1.getId(), bookingDto)
        );

        assertEquals("user with id = 1 can't book his own item", validationException.getMessage());
    }

    @Test
    void approveBooking_whenInvokedWithTrueValue_thenStatusOfBookingChangeToApproved() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any())).thenAnswer(
                invocationOnMock -> {
                    Booking approvedBooking = invocationOnMock.getArgument(0, Booking.class);
                    approvedBooking.setStatus(BookingStatus.APPROVED);
                    return approvedBooking;
                }
        );

        BookingResponseDto responseDto = bookingService.approveBooking(user1.getId(), booking1.getId(), true);

        assertEquals(booking1.getId(), responseDto.getId());
        assertEquals(BookingStatus.APPROVED, responseDto.getStatus());
    }

    @Test
    void approveBooking_whenInvokedWithFalseValue_thenStatusOfBookingChangeToApproved() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any())).thenAnswer(
                invocationOnMock -> {
                    Booking rejectedBooking = invocationOnMock.getArgument(0, Booking.class);
                    rejectedBooking.setStatus(BookingStatus.REJECTED);
                    return rejectedBooking;
                }
        );

        BookingResponseDto responseDto = bookingService.approveBooking(user1.getId(), booking1.getId(), false);

        assertEquals(1, responseDto.getId());
        assertEquals(BookingStatus.REJECTED, responseDto.getStatus());
    }

    @Test
    void approveBooking_whenApproveAlreadyApprovedBooking_thenBookingStatusExceptionThrown() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        booking1.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking1));

        BookingStatusException bookingStatusException = assertThrows(
                BookingStatusException.class, () -> bookingService.approveBooking(
                        user1.getId(),
                        booking1.getId(),
                        true
                )
        );

        assertEquals(
                "it's impossible to change status of booking after approved",
                bookingStatusException.getMessage()
        );
    }

    @Test
    void approveBooking_whenApproveNotTheOwnerOfItem_thenValidationExceptionThrown() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking1));

        ValidationException validationException = assertThrows(
                ValidationException.class, () -> bookingService.approveBooking(
                        user2.getId(),
                        booking1.getId(),
                        true
                )
        );

        assertEquals(
                "user with id = 2 is not the owner for item with id = 1",
                validationException.getMessage()
        );
    }

    @Test
    void findBookingById_whenBookingFoundByOwnerOfItem_thenReturnedBooking() {

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking1));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));

        BookingResponseDto responseDto = bookingService.findBookingById(user1.getId(), booking1.getId());

        assertEquals(booking1.getId(), responseDto.getId());
        assertEquals(BookingStatus.WAITING, responseDto.getStatus());
        assertEquals(booking1.getItem().getName(), responseDto.getItem().getName());
        assertEquals(booking1.getBooker().getId(), responseDto.getBooker().getId());
    }

    @Test
    void findBookingById_whenBookingFoundByBooker_thenReturnedBooking() {

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking1));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));

        BookingResponseDto responseDto = bookingService.findBookingById(user2.getId(), booking1.getId());

        assertEquals(booking1.getId(), responseDto.getId());
        assertEquals(BookingStatus.WAITING, responseDto.getStatus());
        assertEquals(booking1.getItem().getName(), responseDto.getItem().getName());
        assertEquals(booking1.getBooker().getId(), responseDto.getBooker().getId());
    }

    @Test
    void findBookingById_whenSeekerIsNotOwnerOfItemOrBooker_thenValidationExceptionThrown() {

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking1));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));

        ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> bookingService.findBookingById(user3.getId(), booking1.getId())
        );

        assertEquals(
                "user with id = 3 does not have enough rights to view booking with id = 1",
                validationException.getMessage()
        );
    }

    @Test
    void findAllBookingsForCurrentUser_whenStateOfBookingIsWaiting_thenBookingListReturnedWithStatusWaiting() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByBookerIdAndStatusOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForCurrentUser = bookingService.findAllBookingsForCurrentUser(
                user2.getId(),
                BookingState.WAITING,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForCurrentUser.size());
        assertEquals(booking1.getId(), allBookingsForCurrentUser.get(0).getId());
        assertEquals(booking1.getStatus(), allBookingsForCurrentUser.get(0).getStatus());
    }

    @Test
    void findAllBookingsForCurrentUser_whenStateOfBookingIsRejected_thenBookingListReturnedWithStatusRejected() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking2));
        when(bookingRepository
                .getBookingsByBookerIdAndStatusOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForCurrentUser = bookingService.findAllBookingsForCurrentUser(
                user3.getId(),
                BookingState.REJECTED,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForCurrentUser.size());
        assertEquals(booking2.getId(), allBookingsForCurrentUser.get(0).getId());
        assertEquals(booking2.getStatus(), allBookingsForCurrentUser.get(0).getStatus());
    }

    @Test
    void findAllBookingsForCurrentUser_whenStateOfBookingIsFuture_thenBookingListReturnedWithStartOfBookingInFuture() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByBookerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForCurrentUser = bookingService.findAllBookingsForCurrentUser(
                user2.getId(),
                BookingState.FUTURE,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForCurrentUser.size());
        assertEquals(booking1.getId(), allBookingsForCurrentUser.get(0).getId());
        assertTrue(allBookingsForCurrentUser.get(0).getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void findAllBookingsForCurrentUser_whenStateOfBookingIsPast_thenBookingListReturnedWithEndOfBookingInPast() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByBookerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForCurrentUser = bookingService.findAllBookingsForCurrentUser(
                user2.getId(),
                BookingState.PAST,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForCurrentUser.size());
        assertEquals(booking1.getId(), allBookingsForCurrentUser.get(0).getId());
        assertTrue(allBookingsForCurrentUser.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void findAllBookingsForCurrentUser_whenStateOfBookingIsCurrent_thenBookingListReturnedWithEndOfBookingInFutureAndStartInPast() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        booking1.setStart(LocalDateTime.now().minusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(1));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByBookerIdAndCurrentOrderByStartDesc(anyInt(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForCurrentUser = bookingService.findAllBookingsForCurrentUser(
                user2.getId(),
                BookingState.CURRENT,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForCurrentUser.size());
        assertEquals(booking1.getId(), allBookingsForCurrentUser.get(0).getId());
        assertTrue(
                allBookingsForCurrentUser.get(0).getEnd().isAfter(LocalDateTime.now())
                        && allBookingsForCurrentUser.get(0).getStart().isBefore(LocalDateTime.now())
        );
    }

    @Test
    void findAllBookingsForCurrentUser_whenStateOfBookingIsAll_thenReturnedAllBookingsOfUser() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByBookerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForCurrentUser = bookingService.findAllBookingsForCurrentUser(
                user2.getId(),
                BookingState.ALL,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForCurrentUser.size());
        assertEquals(booking1.getId(), allBookingsForCurrentUser.get(0).getId());
        assertEquals(booking1.getStatus(), allBookingsForCurrentUser.get(0).getStatus());
    }

    @Test
    void findAllBookingsForOwnerOfItems_whenStateOfBookingIsWaiting_thenBookingListReturnedWithStatusWaiting() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForOwnerOfItems = bookingService.findAllBookingsForOwnerOfItems(
                user1.getId(),
                BookingState.WAITING,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForOwnerOfItems.size());
        assertEquals(booking1.getId(), allBookingsForOwnerOfItems.get(0).getId());
        assertEquals(booking1.getStatus(), allBookingsForOwnerOfItems.get(0).getStatus());
    }

    @Test
    void findAllBookingsForOwnerOfItems_whenStateOfBookingIsRejected_thenBookingListReturnedWithStatusRejected() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking2));
        when(bookingRepository
                .getBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForOwnerOfItems = bookingService.findAllBookingsForOwnerOfItems(
                user1.getId(),
                BookingState.REJECTED,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForOwnerOfItems.size());
        assertEquals(booking2.getId(), allBookingsForOwnerOfItems.get(0).getId());
        assertEquals(booking2.getStatus(), allBookingsForOwnerOfItems.get(0).getStatus());
    }

    @Test
    void findAllBookingsForOwnerOfItems_whenStateOfBookingIsFuture_thenBookingListReturnedWithStartOfBookingInFuture() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForOwnerOfItems = bookingService.findAllBookingsForOwnerOfItems(
                user1.getId(),
                BookingState.FUTURE,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForOwnerOfItems.size());
        assertEquals(booking1.getId(), allBookingsForOwnerOfItems.get(0).getId());
        assertTrue(allBookingsForOwnerOfItems.get(0).getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void findAllBookingsForOwnerOfItems_whenStateOfBookingIsPast_thenBookingListReturnedWithEndOfBookingInPast() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForOwnerOfItems = bookingService.findAllBookingsForOwnerOfItems(
                user1.getId(),
                BookingState.PAST,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForOwnerOfItems.size());
        assertEquals(booking1.getId(), allBookingsForOwnerOfItems.get(0).getId());
        assertTrue(allBookingsForOwnerOfItems.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void findAllBookingsForOwnerOfItems_whenStateOfBookingIsCurrent_thenBookingListReturnedWithEndOfBookingInFutureAndStartInPast() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        booking1.setStart(LocalDateTime.now().minusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(1));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByItem_OwnerIdAndCurrentOrderByStartDesc(anyInt(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForOwnerOfItems = bookingService.findAllBookingsForOwnerOfItems(
                user1.getId(),
                BookingState.CURRENT,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForOwnerOfItems.size());
        assertEquals(booking1.getId(), allBookingsForOwnerOfItems.get(0).getId());
        assertTrue(
                allBookingsForOwnerOfItems.get(0).getEnd().isAfter(LocalDateTime.now())
                        && allBookingsForOwnerOfItems.get(0).getStart().isBefore(LocalDateTime.now())
        );
    }

    @Test
    void findAllBookingsForOwnerOfItems_whenStateOfBookingIsAll_thenReturnedAllBookingsOfUser() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking1));
        when(bookingRepository
                .getBookingsByItem_OwnerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(bookingPage);

        List<BookingResponseDto> allBookingsForOwnerOfItems = bookingService.findAllBookingsForOwnerOfItems(
                user1.getId(),
                BookingState.ALL,
                PageRequest.ofSize(10));

        assertEquals(1, allBookingsForOwnerOfItems.size());
        assertEquals(booking1.getId(), allBookingsForOwnerOfItems.get(0).getId());
        assertEquals(booking1.getStatus(), allBookingsForOwnerOfItems.get(0).getStatus());
    }

    @Test
    void findAllBookingsForCurrentUser_whenReturnedListIsEmpty_thenValidationExceptionThrown() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        final PageImpl<Booking> bookingPage = new PageImpl<>(Collections.emptyList());
        when(bookingRepository
                .getBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(bookingPage);
        ErrorResponse errorResponse = new ErrorResponse("owner with id = 1 doesn't have any items");

        ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> bookingService.findAllBookingsForOwnerOfItems(
                        user1.getId(),
                        BookingState.WAITING,
                        PageRequest.ofSize(10)
                )
        );

        assertEquals(errorResponse.getError(), validationException.getMessage());
    }

    @Test
    void testModelBooking() {

        Booking bookingTest = new Booking(1, null, null, null, null, null);

        assertTrue(bookingTest.equals(booking1));
    }
}