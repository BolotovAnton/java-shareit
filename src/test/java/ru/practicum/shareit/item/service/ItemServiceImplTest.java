package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.CommentException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    ItemService itemService;
    ItemRepository itemRepository;
    BookingRepository bookingRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    Validation validation;
    BookingMapper bookingMapper;
    ItemRequestRepository itemRequestRepository;
    User user1;
    User user2;
    CommentDto commentDto;
    Comment comment;
    ItemDto itemDto;
    ItemRequest itemRequest;
    Item item1;
    Item item2;
    Booking booking1;
    Booking booking2;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        validation = mock(Validation.class);
        bookingMapper = new BookingMapper(itemRepository, userRepository);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(
                itemRepository,
                bookingRepository,
                userRepository,
                commentRepository,
                validation,
                bookingMapper,
                itemRequestRepository
        );

        user1 = new User(1, "user1", "user1@email.com");
        user2 = new User(2, "user2", "user2@email.com");

        commentDto = new CommentDto(
                1,
                "comment",
                "author",
                LocalDateTime.now()
        );

        comment = new Comment(
              1,
              "comment",
                item1,
              user1,
              LocalDateTime.now()
        );

        itemDto = new ItemDto(
                1,
                "item",
                "testitem",
                true,
                new BookingShortDto(1, 1),
                new BookingShortDto(2, 1),
                Set.of(commentDto),
                1
        );

        item2 = new Item(2, "item2", "testitem2", user1.getId(), true, itemRequest);

        itemRequest = new ItemRequest(1, "itemrequest", user2, LocalDateTime.now(), Set.of(item2));

        item1 = new Item(
                1,
                "item",
                "testitem",
                user1.getId(),
                true,
                Set.of(new Comment(1, "comment", item2, user2, LocalDateTime.now())),
                itemRequest
        );

        booking1 = new Booking(
                1,
                item1,
                user2,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                BookingStatus.WAITING
        );

        booking2 = new Booking(
                2,
                item1,
                user2,
                LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(5),
                BookingStatus.WAITING
        );
    }

    @Test
    void addItem_whenInvoked_thenItemSaved() {

        when(itemRepository.save(any())).thenReturn(item1);
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));

        ItemDto responseItemDto = itemService.addItem(user1.getId(), itemDto);

        assertNotNull(responseItemDto);
        assertEquals(item1.getId(), responseItemDto.getId());
        assertEquals(item1.getRequest().getId(), responseItemDto.getRequestId());

    }

    @Test
    void addItem_whenInvokedWithEmptyItemRequest_thenItemSaved() {

        when(itemRepository.save(any())).thenReturn(item2);
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        itemDto.setRequestId(0);

        ItemDto responseItemDto = itemService.addItem(user1.getId(), itemDto);

        assertNotNull(responseItemDto);
        assertEquals(item2.getId(), responseItemDto.getId());
        assertNull(responseItemDto.getRequestId());

    }

    @Test
    void updateItem_whenNameDescriptionAvailableAreNotNull_thenItemDtoReturned() {

        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto responseItemDto = itemService.updateItem(user1.getId(), item1.getId(), itemDto);

        assertNotNull(responseItemDto);
        assertEquals(item1.getId(), responseItemDto.getId());
    }

    @Test
    void updateItem_whenNameDescriptionAvailableAreEmpty_thenItemDtoReturned() {

        when(itemRepository.save(any())).thenReturn(item1);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item1));
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);

        ItemDto responseItemDto = itemService.updateItem(user1.getId(), item1.getId(), itemDto);

        assertNotNull(responseItemDto);
        assertNotNull(responseItemDto.getName());
        assertNotNull(responseItemDto.getDescription());
        assertNotNull(responseItemDto.getAvailable());
        assertEquals(item1.getId(), responseItemDto.getId());
    }

    @Test
    void findItemById() {

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item1));

        ItemDto responseItemDto = itemService.findItemById(user1.getId(), item1.getId());

        assertNotNull(responseItemDto);
        assertEquals(item1.getId(), responseItemDto.getId());
    }

    @Test
    void findAllItemsForUser_whenItemOwnerFind_thenNextAndLastBookingAreNotEmpty() {

        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item1));
        when(itemRepository.findAllByOwnerId(any(), any())).thenReturn(itemPage);
        when(bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(any(), any())).thenReturn(booking2);
        when(bookingRepository.findFirstByItemAndStartBeforeOrderByStartDesc(any(), any())).thenReturn(booking1);


        List<ItemDto> items = itemService.findAllItemsForUser(user1.getId(), PageRequest.ofSize(10));

        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertNotNull(items.get(0).getNextBooking());
        assertNotNull(items.get(0).getLastBooking());
    }

    @Test
    void findAllItemsForUser_whenNotItemOwnerFind_thenNextAndLastBookingAreEmpty() {

        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item1));
        when(itemRepository.findAllByOwnerId(any(), any())).thenReturn(itemPage);
        when(bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(any(), any())).thenReturn(booking2);
        when(bookingRepository.findFirstByItemAndStartBeforeOrderByStartDesc(any(), any())).thenReturn(booking1);


        List<ItemDto> items = itemService.findAllItemsForUser(user2.getId(), PageRequest.ofSize(10));

        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertNull(items.get(0).getNextBooking());
        assertNull(items.get(0).getLastBooking());
    }

    @Test
    void searchItemsByText_whenTextIsNotBlank_thenItemDtoListReturned() {

        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item1));
        when(itemRepository.findAllByAvailableIsTrueAndNameAndDescriptionContainingIgnoreCase(any(), any()))
                .thenReturn(itemPage);

        List<ItemDto> items = itemService.searchItemsByText("test", PageRequest.ofSize(10));

        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertTrue(items.get(0).getDescription().contains("test"));
    }

    @Test
    void searchItemsByText_whenTextIsBlank_thenEmptyListReturned() {

        List<ItemDto> items = itemService.searchItemsByText(" ", PageRequest.ofSize(10));

        assertTrue(items.isEmpty());
    }

    @Test
    void addComment_whenTextIsNotBlankAndItemListIsNotEmpty_thenCommentAdded() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user1));
        when(itemRepository
                .findItemsByIdAndBookerIdAndEndBeforeCurrent(anyInt(), anyInt(), any()))
                .thenReturn(Collections.singletonList(item1));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto responseCommentDto = itemService.addComment(user1.getId(), item1.getId(), commentDto);

        assertNotNull(responseCommentDto);
        assertEquals(comment.getId(), responseCommentDto.getId());
    }

    @Test
    void addComment_whenTextIsBlank_thenCommentExceptionThrown() {

        commentDto.setText(" ");

        CommentException commentException = assertThrows(
                CommentException.class,
                () -> itemService.addComment(user1.getId(), item1.getId(), commentDto)
        );
        assertEquals("text of comment is empty", commentException.getMessage());
    }

    @Test
    void addComment_whenItemListIsEmpty_thenCommentExceptionThrown() {

        when(itemRepository
                .findItemsByIdAndBookerIdAndEndBeforeCurrent(anyInt(), anyInt(), any()))
                .thenReturn(Collections.emptyList());

        CommentException commentException = assertThrows(
                CommentException.class,
                () -> itemService.addComment(user1.getId(), item1.getId(), commentDto)
        );
        assertEquals("user with id = 1 didn't book the item", commentException.getMessage());
    }

    @Test
    void testModelItem() {

        Item itemTest = new Item(1, null, null, 0, null);

        assertTrue(itemTest.equals(item1));
    }
}