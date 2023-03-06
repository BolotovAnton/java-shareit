package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Booking mapToBooking(BookingDto bookingDto, Integer userId) {
        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(bookingDto.getItemId()).orElseThrow());
        booking.setBooker(userRepository.findById(userId).orElseThrow());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public BookingResponseDto mapToBookingResponseDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setItem(ItemMapper.mapToItemShortDto(itemRepository.findById(booking.getItem().getId()).orElseThrow()));
        bookingResponseDto.setBooker(UserMapper.mapToBookerDto(booking.getBooker().getId()));
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }

    public List<BookingResponseDto> mapToBookingResponseDto(List<Booking> bookingList) {
        List<BookingResponseDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(mapToBookingResponseDto(booking));
        }
        return bookingDtoList;
    }

    public BookingShortDto mapToBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            BookingShortDto bookingShortDto = new BookingShortDto();
            bookingShortDto.setId(booking.getId());
            bookingShortDto.setBookerId(booking.getBooker().getId());
            return bookingShortDto;
        }
    }
}
