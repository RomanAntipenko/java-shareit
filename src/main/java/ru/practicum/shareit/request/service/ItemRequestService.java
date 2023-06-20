package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequest createRequest(long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequest> getRequestsByRequestor(long userId);

    ItemRequest getRequestByRequestId(long userId, long requestId);

    Collection<ItemRequest> getRequestsWithPagination(long userId, Integer from, Integer size);
}

