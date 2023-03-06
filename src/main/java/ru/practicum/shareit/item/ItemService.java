package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto findItemById(Integer userId, Integer itemId);

    List<ItemDto> findAllItemsForUser(Integer userId, PageRequest pageRequest);

    List<ItemDto> searchItemsByText(String text, PageRequest pageRequest);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
