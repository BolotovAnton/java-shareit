package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.UserDidNotBookTheItemException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validation;

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
    private final CommentRepository commentRepository;
    private final Validation validation;
    private final BookingMapper bookingMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        validation.validateUser(userId);
        Item item = ItemMapper.mapToItem(itemDto, userId);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(
                    () -> new ValidationException("request with id " + itemDto.getRequestId() + " not found")
            ));
        }
        Item savedItem = itemRepository.save(item);
        log.info("item has been added");
        return ItemMapper.mapToItemDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        validation.validateUser(userId);
        Item itemFromRepository = itemRepository.findById(itemId).orElseThrow(() -> new ValidationException(
                "item with id " + itemId + " not found"
        ));
        if (itemRepository.findById(itemId).orElseThrow().getOwnerId() != userId) {
            log.info("user with id " + userId + " can't update item with id " + itemId);
            throw new ValidationException("user with id " + userId + " can't update item with id " + itemId);
        }
        if (itemDto.getName() == null) {
            itemDto.setName(itemFromRepository.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemFromRepository.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemFromRepository.getAvailable());
        }
        Item itemForUpdate = ItemMapper.mapToItem(itemDto, userId);
        itemForUpdate.setId(itemId);
        itemRepository.save(itemForUpdate);
        log.info("item has been updated");
        return ItemMapper.mapToItemDto(itemForUpdate);
    }

    @Transactional
    @Override
    public ItemDto findItemById(Integer userId, Integer itemId) {
        validation.validateUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidationException(
                "item with id " + itemId + " not found"
        ));
        ItemDto itemDto = setNextAndLastBookingForItem(item, LocalDateTime.now(), userId);
        log.info("item has been found");
        return itemDto;
    }

    @Override
    public List<ItemDto> findAllItemsForUser(Integer userId, PageRequest pageRequest) {
        validation.validateUser(userId);
        Page<Item> itemList = itemRepository.findAllByOwnerId(userId, pageRequest);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(setNextAndLastBookingForItem(item, LocalDateTime.now(), userId));
        }
        log.info("items has been found");
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItemsByText(String text, PageRequest pageRequest) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Page<Item> itemList = itemRepository.findAllByAvailableIsTrueAndNameAndDescriptionContainingIgnoreCase(
                text,
                pageRequest
        );
        log.info("items has been found by text " + text);
        return ItemMapper.mapToItemDto(itemList);
    }

    @Transactional
    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        User user = validation.validateUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidationException(
                "item with id " + itemId + " not found"
        ));
        if (itemRepository.findItemsByIdAndBookerIdAndEndBeforeCurrent(itemId, userId).isEmpty()) {
            log.info("user with id = " + userId + " didn't book the item");
            throw new UserDidNotBookTheItemException("user with id = " + userId + " didn't book the item");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        Comment saveComment = commentRepository.save(comment);
        log.info("comment has been added");
        return CommentMapper.mapToCommentDto(saveComment);
    }

    private ItemDto setNextAndLastBookingForItem(Item item, LocalDateTime localDateTime, Integer userId) {
        Booking nextBooking = bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(item, localDateTime);
        Booking lastBooking = bookingRepository.findFirstByItemAndStartBeforeOrderByStartDesc(item, localDateTime);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        if (item.getOwnerId() == userId) {
            itemDto.setNextBooking(bookingMapper.mapToBookingShortDto(nextBooking));
            itemDto.setLastBooking(bookingMapper.mapToBookingShortDto(lastBooking));
        }
        return itemDto;
    }
}
