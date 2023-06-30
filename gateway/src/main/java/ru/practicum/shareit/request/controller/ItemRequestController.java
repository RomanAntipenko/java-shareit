package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Validated ItemRequestDto itemRequestDto) {
        log.info("Создеам запрос {}, userId={}", itemRequestDto, userId);
        return requestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вызван метод получения списка запросов на предмет для создателя запроса");
        return requestClient.getRequestsByRequestor(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByRequestId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @PathVariable long requestId) {
        log.info("Вызван метод получения списка запросов на предмет для создателя запроса, {}", requestId);
        return requestClient.getRequestByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @PositiveOrZero @RequestParam(name = "from",
                                                                    defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(name = "size",
                                                                    defaultValue = "10") Integer size) {
        log.info("Вызван метод получения списка запросов на предмет с пагинацией userId={}, from={}, size={}",
                userId, from, size);
        return requestClient.getRequestsWithPagination(userId, from, size);
    }
}
