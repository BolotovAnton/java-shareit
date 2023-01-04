package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.CommentException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer userId, ItemDto itemDto) throws ValidationException;

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) throws ValidationException;

    ItemDto findItemById(Integer userId, Integer itemId) throws ValidationException;

    List<ItemDto> findAllItemsForUser(Integer userId) throws ValidationException;

    List<ItemDto> searchItemsByText(String text);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) throws ValidationException, CommentException;
}
