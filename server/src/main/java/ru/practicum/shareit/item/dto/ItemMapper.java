package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemMapper {
    public ItemDto mapToDto(Item item) {
        if (item.getRequest() != null) {
            return ItemDto.builder()
                    .id(item.getId())
                    .description(item.getDescription())
                    .name(item.getName())
                    .available(item.getAvailable())
                    .requestId(item.getRequest().getId())
                    .build();
        }
        return ItemDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
    }

    public Item mapToItem(User user, ItemDto itemDto) {
        Item item = new Item();
        item.setOwner(user);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public Item mapToItem(long id, User user, ItemDto itemDto) {
        Item item = new Item();
        item.setId(id);
        item.setOwner(user);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
