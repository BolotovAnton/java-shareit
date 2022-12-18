package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item updateItem(int itemId, Item item);

    Item findItemById(Integer itemId);

    List<Item> findAllItemsForUser(int userId);

    List<Item> findAllItems();
}
