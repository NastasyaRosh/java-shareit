package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item createItem(Item item);

    Item updateItem(Item item, Long itemId, Long userId);

    Optional<Item> getItem(Long itemId);

    List<Item> getAllMyItems(Long userId);

    List<Item> searchItems(String text);
}
