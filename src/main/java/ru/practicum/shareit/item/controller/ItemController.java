package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validations.FirstlyItemValidation;
import ru.practicum.shareit.item.validations.SecondaryItemValidation;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Validated({SecondaryItemValidation.class,
                                      FirstlyItemValidation.class}) @RequestBody ItemDto itemDto) {
        log.info("Вызван метод создания предмета, в ItemController");
        return ItemMapper.mapToDto(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long itemId,
                             @RequestBody ItemDto itemDto) {
        log.info("Вызван метод обновления предмета, в ItemController");
        return ItemMapper.mapToDto(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping
    public Collection<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вызван метод получения списка предметов для владельца, в ItemController");
        return itemService.getAllItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable long itemId) {
        log.info("Вызван метод получения предмета, в ItemController");
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemByText(@RequestParam("text") String text) {
        log.info("Вызван метод поиска предмета, в ItemController");
        return itemService.searchItem(text).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Вызван метод добавления отзыва после бронирования, в ItemController");
        return itemService.postComment(userId, itemId, commentDto);
    }
}
