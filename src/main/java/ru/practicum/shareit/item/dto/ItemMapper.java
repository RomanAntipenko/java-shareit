package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
    }

    public static Item mapToItem(long userId, ItemDto itemDto) {
        return Item.builder()
                .userId(userId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item mapToItem(long id, long userId, ItemDto itemDto) {
        return Item.builder()
                .id(id)
                .userId(userId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
