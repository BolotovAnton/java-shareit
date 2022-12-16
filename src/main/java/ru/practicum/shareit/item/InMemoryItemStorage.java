package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private static int generateId = 1;

    private final HashMap<Integer, Item> items = new HashMap<>();

    private static Integer getNextId() {
        return generateId++;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(int itemId, Item item) {
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item findItemById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllItemsForUser(int userId) {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findAllItems() {
        return new ArrayList<>(items.values());
    }
}
