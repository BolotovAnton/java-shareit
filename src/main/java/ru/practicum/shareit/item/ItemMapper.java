package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
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

    public static ItemShortDto mapToItemShortDto(ItemRepository itemRepository, Integer itemId) {
        return new ItemShortDto(
                itemId,
                itemRepository.findById(itemId).orElseThrow().getName()
        );
    }
}
