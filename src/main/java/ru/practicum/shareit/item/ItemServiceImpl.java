package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.validation.Validation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) throws ValidationException {
        Validation.validateUserId(userStorage, userId);
        Item item = itemStorage.addItem(ItemMapper.mapToItem(itemDto, userId));
        log.info("item has been added");
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) throws ValidationException {
        Validation.validateUserId(userStorage, userId);
        Validation.validateItemId(itemStorage, itemId);
        Validation.validateUserIdForItem(itemStorage, userId, itemId);
        if (itemDto.getName() == null) {
            itemDto.setName(itemStorage.findItemById(itemId).getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemStorage.findItemById(itemId).getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemStorage.findItemById(itemId).getAvailable());
        }
        Item item = itemStorage.updateItem(itemId, ItemMapper.mapToItem(itemDto, userId));
        log.info("item has been updated");
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto findItemById(Integer itemId) throws ValidationException {
        Validation.validateItemId(itemStorage, itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(itemStorage.findItemById(itemId));
        log.info("item has been found");
        return itemDto;
    }

    @Override
    public List<ItemDto> findAllItemsForUser(int userId) throws ValidationException {
        Validation.validateUserId(userStorage, userId);
        List<Item> itemList = itemStorage.findAllItemsForUser(userId)
                .stream()
                .filter(item -> item.getUserId() == userId)
                .collect(Collectors.toList());
        log.info("items has been found");
        return ItemMapper.mapToItemDto(itemList);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String textToLowerCase = text.toLowerCase();
        List<Item> itemList = itemStorage.findAllItems()
                .stream()
                .filter(x -> x.getDescription().toLowerCase().contains(textToLowerCase))
                .filter(x -> x.getAvailable().equals(true))
                .collect(Collectors.toList());
        log.info("items has been found by text " + text);
        return ItemMapper.mapToItemDto(itemList);
    }
}
