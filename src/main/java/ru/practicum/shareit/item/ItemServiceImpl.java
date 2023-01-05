package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.CommentException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.validation.Validation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final Validation validation;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        validation.validateUserId(userId);
        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, userId));
        log.info("item has been added");
        return ItemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        validation.validateUserId(userId);
        validation.validateItemId(itemId);
        validation.validateUserIdForItem(userId, itemId);
        if (itemDto.getName() == null) {
            itemDto.setName(itemRepository.findById(itemId).orElseThrow().getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemRepository.findById(itemId).orElseThrow().getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemRepository.findById(itemId).orElseThrow().getAvailable());
        }
        Item item = ItemMapper.mapToItem(itemDto, userId);
        item.setId(itemId);
        itemRepository.save(item);
        log.info("item has been updated");
        return ItemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto findItemById(Integer userId, Integer itemId) {
        validation.validateItemId(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow();
        ItemDto itemDto = setNextAndLastBookingForItem(item, LocalDateTime.now(), userId);
        log.info("item has been found");
        return itemDto;
    }

    @Override
    public List<ItemDto> findAllItemsForUser(Integer userId) {
        validation.validateUserId(userId);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(setNextAndLastBookingForItem(item, LocalDateTime.now(), userId));
        }
        log.info("items has been found");
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemRepository.findAllByAvailableIsTrueAndNameAndDescriptionContainingIgnoreCase(text);
        log.info("items has been found by text " + text);
        return ItemMapper.mapToItemDto(itemList);
    }

    @Transactional
    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        validation.validateUserId(userId);
        validation.validateItemId(itemId);
        if (commentDto.getText().isBlank()) {
            log.info("text of comment is empty");
            throw new CommentException("text of comment is empty");
        }
        if (itemRepository.findItemsByIdAndBookerIdAndEndBeforeCurrent(itemId, userId, LocalDateTime.now()).isEmpty()) {
            log.info("user with id = " + userId + " didn't book the item");
            throw new CommentException("user with id = " + userId + " didn't book the item");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(itemRepository.findById(itemId).orElseThrow());
        comment.setAuthor(userRepository.findById(userId).orElseThrow());
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("comment has been added");
        return CommentMapper.mapToCommentDto(comment);
    }

    private ItemDto setNextAndLastBookingForItem(Item item, LocalDateTime localDateTime, Integer userId) {
        Booking nextBooking = bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(item, localDateTime);
        Booking lastBooking = bookingRepository.findFirstByItemAndStartBeforeOrderByStartDesc(item, localDateTime);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        if (nextBooking != null && lastBooking != null && item.getOwnerId() == userId) {
            itemDto.setNextBooking(bookingMapper.mapToBookingResponse2Dto(nextBooking));
            itemDto.setLastBooking(bookingMapper.mapToBookingResponse2Dto(lastBooking));
        }
        return itemDto;
    }
}
