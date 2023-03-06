package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    Booking booking1;
    Booking booking2;
    Booking booking3;
    Item item;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp() {

        user1 = userRepository.save(new User(1, "user1", "user1@email.com"));
        user2 = userRepository.save(new User(2, "user2", "user2@email.com"));
        user3 = userRepository.save(new User(3, "user3", "user3@email.com"));
        item = itemRepository.save(new Item(1, "item1", "test item", user1.getId(), true));

        booking1 = bookingRepository.save(new Booking(
                1,
                item,
                user2,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                BookingStatus.WAITING
        ));

        booking2 = bookingRepository.save(new Booking(
                2,
                item,
                user3,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.WAITING
        ));

        booking3 = bookingRepository.save(new Booking(
                3,
                item,
                user3,
                LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(5),
                BookingStatus.WAITING
        ));
    }

    @Test
    void getBookingsByBookerIdOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByBookerIdOrderByStartDesc(
                user2.getId(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByBookerIdAndStatusOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndStatusOrderByStartDesc(
                user2.getId(),
                BookingStatus.WAITING,
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByBookerIdAndEndBeforeOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndEndBeforeOrderByStartDesc(
                user2.getId(),
                LocalDateTime.now().plusHours(4),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByBookerIdAndStartAfterOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndStartAfterOrderByStartDesc(
                user2.getId(),
                LocalDateTime.now(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByBookerIdAndCurrentOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndCurrentOrderByStartDesc(
                user3.getId(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(1, bookings.size());
    }

    @Test
    void findFirstByItemAndStartBeforeOrderByStartDesc() {

        Booking booking = bookingRepository.findFirstByItemAndStartBeforeOrderByStartDesc(
                item,
                LocalDateTime.now().plusHours(3).plusMinutes(30)
        );

        assertEquals(booking1.getId(), booking.getId());
    }

    @Test
    void findFirstByItemAndStartAfterOrderByStartAsc() {

        Booking booking = bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(
                item,
                LocalDateTime.now().plusHours(3).plusMinutes(30)
        );

        assertEquals(booking3.getId(), booking.getId());
    }

    @Test
    void getBookingsByItem_OwnerIdOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByItem_OwnerIdOrderByStartDesc(
                item.getOwnerId(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(3, bookings.size());
    }

    @Test
    void getBookingsByItem_OwnerIdAndStatusOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByItem_OwnerIdAndStatusOrderByStartDesc(
                item.getOwnerId(),
                BookingStatus.WAITING,
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(3, bookings.size());
    }

    @Test
    void getBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(
                item.getOwnerId(),
                LocalDateTime.now().plusHours(4),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(2, bookings.size());
    }

    @Test
    void getBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(
                item.getOwnerId(),
                LocalDateTime.now().plusHours(3).plusMinutes(30),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByItem_OwnerIdAndCurrentOrderByStartDesc() {

        List<Booking> bookings = bookingRepository.getBookingsByItem_OwnerIdAndCurrentOrderByStartDesc(
                item.getOwnerId(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "start"))
        ).toList();

        assertEquals(1, bookings.size());
    }
}