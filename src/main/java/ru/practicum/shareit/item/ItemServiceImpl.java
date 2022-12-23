package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Validation;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final Validation validation;

    @Transactional
    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) throws ValidationException {
        validation.validateUserId(userId);
        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, userId));
        log.info("item has been added");
        return ItemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) throws ValidationException {
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

    @Override
    public ItemDto findItemById(Integer itemId) throws ValidationException {
        validation.validateItemId(itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(itemRepository.findById(itemId).orElseThrow());
        log.info("item has been found");
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> findAllItemsForUser(Integer userId) throws ValidationException {
        validation.validateUserId(userId);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId);
        log.info("items has been found");
        return ItemMapper.mapToItemDto(itemList);
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
}
