package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(long userId, ItemDto itemDto);

    Item updateItem(long userId, long itemId, ItemDto item);

    Collection<ItemDto> getAllItemsByOwner(long userId, Integer from, Integer size);

    Collection<Item> searchItem(String text, Integer from, Integer size);

    ItemDto getItemById(long userId, long itemId);

    Comment postComment(long userId, long itemId, CommentDto commentDto);
}
