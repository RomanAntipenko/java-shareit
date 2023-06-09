package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

import static ru.practicum.shareit.ConstantsForServer.userIdHeader;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(userIdHeader) long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Вызван метод создания предмета, в ItemController");
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader(userIdHeader) long userId,
                             @PathVariable long itemId,
                             @RequestBody ItemDto itemDto) {
        log.info("Вызван метод обновления предмета, в ItemController");
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByOwner(@RequestHeader(userIdHeader) long userId,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        log.info("Вызван метод получения списка предметов для владельца, в ItemController");
        return itemService.getAllItemsByOwner(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(userIdHeader) long userId,
                               @PathVariable long itemId) {
        log.info("Вызван метод получения предмета, в ItemController");
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemByText(@RequestHeader(userIdHeader) long userId,
                                             @RequestParam("text") String text,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        log.info("Вызван метод поиска предмета, в ItemController");
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(userIdHeader) long userId,
                                    @PathVariable long itemId,
                                    @RequestBody CommentDto commentDto) {
        log.info("Вызван метод добавления отзыва после бронирования, в ItemController");
        return itemService.postComment(userId, itemId, commentDto);
    }
}
