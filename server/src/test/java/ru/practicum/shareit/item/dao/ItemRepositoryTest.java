package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    Item item1;
    Item item2;
    User user1;
    User user2;
    Booking booking;

    @BeforeEach
    void setUp() {

        user1 = userRepository.save(new User(1, "user1", "user1@email.com"));
        user2 = userRepository.save(new User(2, "user2", "user2@email.com"));
        item1 = itemRepository.save(new Item(1, "item1", "test item1", user1.getId(), true));
        item2 = itemRepository.save(new Item(2, "item2", "test item2", user2.getId(), true));

        booking = bookingRepository.save(new Booking(
                1,
                item1,
                user2,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                BookingStatus.WAITING
        ));
    }

    @Test
    void findAllByOwnerId() {

        List<Item> items = itemRepository.findAllByOwnerId(
                user1.getId(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"))
        ).toList();

        assertEquals(1, items.size());
    }

    @Test
    void findAllByAvailableIsTrueAndNameAndDescriptionContainingIgnoreCase() {

        List<Item> items = itemRepository.findAllByAvailableIsTrueAndNameAndDescriptionContainingIgnoreCase(
                "item1",
                PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"))
        ).toList();

        assertEquals(1, items.size());
    }

    @Test
    void findItemsByIdAndBookerIdAndEndBeforeCurrent() {

        List<Item> items = itemRepository.findItemsByIdAndBookerIdAndEndBeforeCurrent(
                item1.getId(),
                user2.getId(),
                LocalDateTime.now());

        assertEquals(1, items.size());
    }
}