package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.exceptions.RequestIdNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto createRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserIdNotFoundException(
                String.format("userId: \"%s\" не найден", userId)));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(user, Collections.emptyList(),
                LocalDateTime.now(), itemRequestDto);
        return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestDto> getRequestsByRequestor(long userId) {
        if (!userRepository.existsById(userId)) {
            log.debug("User id not found in getRequestsByRequestor method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", userId));
        }
        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRequests.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestByRequestId(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            log.debug("User id not found in getRequestByRequestId method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new RequestIdNotFoundException(String.format("requestId: \"%s\" не найден", requestId)));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public Collection<ItemRequestDto> getRequestsWithPagination(long userId, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequestList;
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        itemRequestList = itemRequestRepository.findAllWithPagination(userId, pageable);
        if (itemRequestList.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRequestList.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }
}
