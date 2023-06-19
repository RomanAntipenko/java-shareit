package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest.getItems() != null || !itemRequest.getItems().isEmpty()) {
            return ItemRequestDto.builder()
                    .id(itemRequest.getId())
                    .description(itemRequest.getDescription())
                    .created(itemRequest.getCreated())
                    .items(itemRequest.getItems().stream()
                            .map(ItemMapper::mapToDto)
                            .collect(Collectors.toList()))
                    .build();
        } else {
            return ItemRequestDto.builder()
                    .id(itemRequest.getId())
                    .description(itemRequest.getDescription())
                    .created(itemRequest.getCreated())
                    .items(Collections.emptyList())
                    .build();
        }
    }

    public ItemRequest mapToItemRequest(User requestor, List<Item> items, LocalDateTime created,
                                        ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setItems(items);
        return itemRequest;
    }
}
