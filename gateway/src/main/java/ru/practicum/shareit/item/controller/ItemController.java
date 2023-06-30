package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.validations.FirstlyItemValidation;
import ru.practicum.shareit.item.validations.SecondaryItemValidation;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Validated({SecondaryItemValidation.class,
                                                     FirstlyItemValidation.class}) @RequestBody ItemDto itemDto) {
        log.info("Создеам вещь {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long itemId,
                                            @RequestBody ItemDto itemDto) {
        log.info("Обновляем вещь {}, userId={}, itemId={}", itemDto, userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping //пагинаця
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                  Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10")
                                                  Integer size) {
        log.info("Получаем вещи по id владельца, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItemsByOwner(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.info("Получаем вещи по id вещи, userId={}, itemId={}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search") //пагинация??
    public ResponseEntity<Object> getItemByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam("text") String text,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {
        log.info("Получаем вещи по тексту {}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Вызван метод добавления отзыва после бронирования {}, userId={}, itemId={}", commentDto, userId,
                itemId);
        return itemClient.postComment(userId, itemId, commentDto);
    }
}
