/*package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDaoMemoryImpl implements ItemDao {
    private Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public Item createItem(Item item) {
        items.put(setId(item), item);
        return items.get(item.getId());
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        item.setId(itemId);
        items.replace(itemId, item);
        return items.get(itemId);
    }

    @Override
    public Optional<Item> getItem(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAllMyItems(Long userId) {
        return items.values().stream()
                .filter(item -> (item.getOwner().getId() == userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private long setId(Item item) {
        item.setId(++this.id);
        return this.id;
    }
}*/
