package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validations.FirstlyItemValidation;
import ru.practicum.shareit.item.validations.SecondaryItemValidation;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Validated({SecondaryItemValidation.class,
                                      FirstlyItemValidation.class}) @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(userId, itemDto);
        return ItemMapper.mapToDto(itemService.createItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long itemId,
                             @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemId, userId, itemDto);
        return ItemMapper.mapToDto(itemService.updateItem(item));
    }

    @GetMapping
    public Collection<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsByOwner(userId).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable long itemId) {
        return ItemMapper.mapToDto(itemService.getItemById(itemId));
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemByText(@RequestParam("text") String text) {
        return itemService.searchItem(text).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
