package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto findItemById(Integer userId, Integer itemId);

    List<ItemDto> findAllItemsForUser(Integer userId);

    List<ItemDto> searchItemsByText(String text);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
