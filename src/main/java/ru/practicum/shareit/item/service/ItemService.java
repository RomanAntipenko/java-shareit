package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(long userId, ItemDto itemDto);

    Item updateItem(long userId, long itemId, ItemDto item);

    Collection<ItemDto> getAllItemsByOwner(long userId);

    Collection<Item> searchItem(String text);

    ItemDto getItemById(long userId, long itemId);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

}
