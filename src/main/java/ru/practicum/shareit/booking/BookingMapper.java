package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static Booking mapToBooking(BookingDto bookingDto, Integer userId) {
        Booking booking = new Booking();
        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(userId);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static BookingResponseDto mapToBookingResponseDto(ItemRepository itemRepository, Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
                bookingResponseDto.setId(booking.getId());
                bookingResponseDto.setItem(ItemMapper.mapToItemShortDto(itemRepository, booking.getItemId()));
                bookingResponseDto.setBooker(UserMapper.mapToBookerDto(booking.getBookerId()));
                bookingResponseDto.setStart(booking.getStart());
                bookingResponseDto.setEnd(booking.getEnd());
                bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }

    public static List<BookingResponseDto> mapToBookingResponseDto(ItemRepository itemRepository, List<Booking> bookingList) {
        List<BookingResponseDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(mapToBookingResponseDto(itemRepository, booking));
        }
        return bookingDtoList;
    }
}
