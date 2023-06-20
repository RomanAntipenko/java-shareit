package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.exceptions.PaginationNotValidException;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequest createRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserIdNotFoundException(
                String.format("userId: \"%s\" не найден", userId)));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(user, Collections.emptyList(),
                LocalDateTime.now(), itemRequestDto);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public Collection<ItemRequest> getRequestsByRequestor(long userId) {
        if (!userRepository.existsById(userId)) {
            log.debug("User id not found in getRequestsByRequestor method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", userId));
        }
        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRequests;
    }

    @Override
    public ItemRequest getRequestByRequestId(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            log.debug("User id not found in getRequestByRequestId method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new RequestIdNotFoundException(String.format("requestId: \"%s\" не найден", requestId)));
        return itemRequest;
    }

    @Override
    public Collection<ItemRequest> getRequestsWithPagination(long userId, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequestList;
        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new PaginationNotValidException("Переданы некорректные данные для пагинации");
            }
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, sort);
            itemRequestList = itemRequestRepository.findAllWithPagination(userId, pageable);
        } else {
            itemRequestList = itemRequestRepository.findAllWithPagination(userId, sort);
        }
        if (itemRequestList.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRequestList;
    }
}
