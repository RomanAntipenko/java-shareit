package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Item item);

    Collection<Item> getItemsByUserId(long userId);

    Collection<Item> getItemsByText(String text);

    Item getItemByItemId(long itemId);
}
