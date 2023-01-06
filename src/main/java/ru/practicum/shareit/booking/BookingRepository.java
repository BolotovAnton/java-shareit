package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> getBookingsByOrderByStartDesc();

    List<Booking> getBookingsByStatusOrderByStartDesc(BookingStatus status);

    List<Booking> getBookingsByEndBeforeOrderByStartDesc(LocalDateTime localDateTime);

    List<Booking> getBookingsByStartAfterOrderByStartDesc(LocalDateTime localDateTime);

    @Query("select b from Booking as b where current_timestamp between b.start and b.end")
    List<Booking> getBookingsByCurrentOrderByStartDesc();

    Booking findFirstByItemAndStartBeforeOrderByStartDesc(Item item, LocalDateTime start);

    Booking findFirstByItemAndStartAfterOrderByStartAsc(Item item, LocalDateTime start);
}
