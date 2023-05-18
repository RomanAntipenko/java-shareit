package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(Item item);

    Item patchItem(Item item);

    Collection<Item> getAllItemsByOwner(long userId);

    Collection<Item> searchItem(String text);

    Item getItemById(long itemId);

}
