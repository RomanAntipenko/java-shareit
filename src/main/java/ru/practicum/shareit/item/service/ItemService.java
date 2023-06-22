package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto item);

    Collection<ItemDto> getAllItemsByOwner(long userId, Integer from, Integer size);

    Collection<ItemDto> searchItem(String text, Integer from, Integer size);

    ItemDto getItemById(long userId, long itemId);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);
}
