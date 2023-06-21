package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createRequest(long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getRequestsByRequestor(long userId);

    ItemRequestDto getRequestByRequestId(long userId, long requestId);

    Collection<ItemRequestDto> getRequestsWithPagination(long userId, Integer from, Integer size);
}

