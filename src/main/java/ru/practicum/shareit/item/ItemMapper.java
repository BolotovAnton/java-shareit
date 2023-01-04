package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setComments(CommentMapper.mapToCommentDto(item.getComments()));
        return itemDto;
    }

    public static Item mapToItem(ItemDto itemDto, Integer userId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwnerId(userId);
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items) {
            itemDtoList.add(mapToItemDto(item));
        }
        return itemDtoList;
    }

    public static ItemShortDto mapToItemShortDto(Item item) {
        return new ItemShortDto(
                item.getId(),
                item.getName()
        );
    }
}
