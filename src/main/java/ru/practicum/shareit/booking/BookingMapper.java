package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse2Dto;
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

    public BookingResponse2Dto mapToBookingResponse2Dto(Booking booking) {
        BookingResponse2Dto bookingResponse2Dto = new BookingResponse2Dto();
        bookingResponse2Dto.setId(booking.getId());
        bookingResponse2Dto.setItem(ItemMapper.mapToItemShortDto(itemRepository.findById(booking.getItem().getId()).orElseThrow()));
        bookingResponse2Dto.setBookerId(booking.getBooker().getId());
        bookingResponse2Dto.setStart(booking.getStart());
        bookingResponse2Dto.setEnd(booking.getEnd());
        bookingResponse2Dto.setStatus(booking.getStatus());
        return bookingResponse2Dto;
    }
}
