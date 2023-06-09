package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

import static ru.practicum.shareit.ConstantsForServer.userIdHeader;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(userIdHeader) long userId,
                                        @RequestBody @Validated ItemRequestDto itemRequestDto) {
        log.info("Вызван метод создания запроса на предмет, в ItemRequestController");
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getRequestsByRequestor(@RequestHeader(userIdHeader) long userId) {
        log.info("Вызван метод получения списка запросов на предмет для создателя запроса, в ItemRequestController");
        return itemRequestService.getRequestsByRequestor(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestByRequestId(@RequestHeader(userIdHeader) long userId,
                                                @PathVariable long requestId) {
        log.info("Вызван метод получения списка запросов на предмет для создателя запроса, в ItemRequestController");
        return itemRequestService.getRequestByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getRequestsWithPagination(@RequestHeader(userIdHeader) long userId,
                                                                @RequestParam(required = false) Integer from,
                                                                @RequestParam(required = false) Integer size) {
        log.info("Вызван метод получения списка запросов на предмет с пагинацией, в ItemRequestController");
        return itemRequestService.getRequestsWithPagination(userId, from, size);
    }
}
