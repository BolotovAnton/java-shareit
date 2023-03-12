package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Page<Booking> getBookingsByBookerIdOrderByStartDesc(Integer bookerId, PageRequest pageRequest);

    Page<Booking> getBookingsByBookerIdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status, PageRequest pageRequest);

    Page<Booking> getBookingsByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime localDateTime, PageRequest pageRequest);

    Page<Booking> getBookingsByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime localDateTime, PageRequest pageRequest);

    @Query("select b from Booking as b where b.booker.id = ?1 and current_timestamp between b.start and b.end order by b.start desc")
    Page<Booking> getBookingsByBookerIdAndCurrentOrderByStartDesc(Integer bookerId, PageRequest pageRequest);

    Booking findFirstByItemAndStartBeforeOrderByStartDesc(Item item, LocalDateTime start);

    Booking findFirstByItemAndStartAfterOrderByStartAsc(Item item, LocalDateTime start);

    Page<Booking> getBookingsByItem_OwnerIdOrderByStartDesc(Integer ownerId, PageRequest pageRequest);

    Page<Booking> getBookingsByItem_OwnerIdAndStatusOrderByStartDesc(Integer ownerId, BookingStatus status, PageRequest pageRequest);

    Page<Booking> getBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime localDateTime, PageRequest pageRequest);

    Page<Booking> getBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime localDateTime, PageRequest pageRequest);

    @Query("select b from Booking as b where b.item.ownerId = ?1 and current_timestamp between b.start and b.end order by b.start desc")
    Page<Booking> getBookingsByItem_OwnerIdAndCurrentOrderByStartDesc(Integer ownerId, PageRequest pageRequest);
}
