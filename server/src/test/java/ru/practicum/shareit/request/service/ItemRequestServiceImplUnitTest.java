package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.exceptions.RequestIdNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    User firstUser;
    User secondUser;
    User thirdUser;
    Item firstItem;
    Item secondItem;
    ItemRequest firstItemRequest;
    ItemRequest firstItemRequestRaw;
    ItemRequestDto firstItemRequestDto;

    @BeforeEach
    void init() {
        firstUser = new User();
        firstUser.setName("maks");
        firstUser.setEmail("maks220@mail.ru");
        firstUser.setId(1L);

        secondUser = new User();
        secondUser.setName("sanya");
        secondUser.setEmail("gera789@mail.ru");
        secondUser.setId(2L);

        thirdUser = new User();
        thirdUser.setName("roma");
        thirdUser.setEmail("romafifa@mail.ru");
        thirdUser.setId(3L);

        firstItem = new Item();
        firstItem.setOwner(firstUser);
        firstItem.setAvailable(true);
        firstItem.setDescription("GamingPC");
        firstItem.setName("PC");
        firstItem.setId(1L);

        secondItem = new Item();
        secondItem.setOwner(secondUser);
        secondItem.setAvailable(false);
        secondItem.setDescription("Beer");
        secondItem.setName("Waizen beer");
        secondItem.setId(2L);

        firstItemRequest = new ItemRequest();
        firstItemRequest.setCreated(LocalDateTime.now());
        firstItemRequest.setRequestor(thirdUser);
        firstItemRequest.setDescription("I want Pc");
        firstItemRequest.setId(1L);
        firstItemRequest.setItems(new ArrayList<>());

        firstItemRequestRaw = new ItemRequest();
        firstItemRequestRaw.setCreated(LocalDateTime.now());
        firstItemRequestRaw.setDescription("I want Pc");
        firstItemRequestRaw.setItems(new ArrayList<>());

        firstItemRequestDto = ItemRequestDto.builder()
                .items(new ArrayList<>())
                .created(firstItemRequestRaw.getCreated())
                .description(firstItemRequestRaw.getDescription())
                .build();
    }

    @Test
    void createRequestAndItsOk() {
        Mockito.when(userRepository.findById(thirdUser.getId()))
                .thenReturn(Optional.of(thirdUser));
        Mockito.when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(firstItemRequest);

        Assertions.assertEquals(ItemRequestMapper.mapToItemRequestDto(firstItemRequest),
                itemRequestService.createRequest(thirdUser.getId(), firstItemRequestDto));
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void createRequestAndUserNotFound() {
        Mockito.when(userRepository.findById(thirdUser.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserIdNotFoundException.class, () -> itemRequestService.createRequest(
                thirdUser.getId(), firstItemRequestDto));
        Mockito.verify(itemRequestRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void getRequestsByRequestorAndItsOk() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(firstItemRequest);
        Mockito.when(userRepository.existsById(thirdUser.getId()))
                .thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(thirdUser.getId()))
                .thenReturn(itemRequests);

        Assertions.assertEquals(itemRequests.stream()
                        .map(ItemRequestMapper::mapToItemRequestDto)
                        .collect(Collectors.toList()),
                itemRequestService.getRequestsByRequestor(thirdUser.getId()));
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdOrderByCreatedDesc(thirdUser.getId());
    }

    @Test
    void getRequestsByRequestorAndEmptyListBack() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        Mockito.when(userRepository.existsById(thirdUser.getId()))
                .thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(thirdUser.getId()))
                .thenReturn(itemRequests);
        Assertions.assertEquals(Collections.emptyList(), itemRequestService.getRequestsByRequestor(thirdUser.getId()));
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdOrderByCreatedDesc(thirdUser.getId());
    }

    @Test
    void getRequestsByRequestorAndUserNotFound() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        Mockito.when(userRepository.existsById(thirdUser.getId()))
                .thenReturn(false);
        Assertions.assertThrows(UserIdNotFoundException.class, () -> itemRequestService.getRequestsByRequestor(
                thirdUser.getId()));
        Mockito.verify(itemRequestRepository, Mockito.times(0))
                .findAllByRequestorIdOrderByCreatedDesc(thirdUser.getId());
    }

    @Test
    void getRequestByRequestIdAndUserNotFound() {
        Mockito.when(userRepository.existsById(thirdUser.getId()))
                .thenReturn(false);
        Assertions.assertThrows(UserIdNotFoundException.class, () -> itemRequestService.getRequestByRequestId(
                thirdUser.getId(), firstItemRequest.getId()));
        Mockito.verify(itemRequestRepository, Mockito.times(0))
                .findById(thirdUser.getId());
    }

    @Test
    void getRequestByRequestIdAndItsOk() {
        Mockito.when(userRepository.existsById(thirdUser.getId()))
                .thenReturn(true);
        Mockito.when(itemRequestRepository.findById(firstItemRequest.getId()))
                .thenReturn(Optional.of(firstItemRequest));

        Assertions.assertEquals(ItemRequestMapper.mapToItemRequestDto(firstItemRequest),
                itemRequestService.getRequestByRequestId(thirdUser.getId(), firstItemRequest.getId()));
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(firstItemRequest.getId());
    }

    @Test
    void getRequestByRequestIdAndRequestNotFound() {
        Mockito.when(userRepository.existsById(thirdUser.getId()))
                .thenReturn(true);
        Mockito.when(itemRequestRepository.findById(99L))
                .thenThrow(RequestIdNotFoundException.class);

        Assertions.assertThrows(RequestIdNotFoundException.class, () ->
                itemRequestService.getRequestByRequestId(thirdUser.getId(), 99L));
    }

    @Test
    void getRequestsWithPaginationAndItsOk() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(firstItemRequest);
        Mockito.when(itemRequestRepository.findAllWithPagination(firstUser.getId(), PageRequest.of(0, 2, sort)))
                .thenReturn(itemRequestList);

        Assertions.assertEquals(itemRequestList.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList()), itemRequestService.getRequestsWithPagination(
                firstUser.getId(), 0, 2));
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllWithPagination(
                firstUser.getId(), PageRequest.of(0, 2, sort));
    }

    @Test
    void getRequestsWithPaginationAndItsRequestor() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequestList = new ArrayList<>();
        Mockito.when(itemRequestRepository.findAllWithPagination(thirdUser.getId(), PageRequest.of(0, 2, sort)))
                .thenReturn(itemRequestList);

        Assertions.assertEquals(Collections.emptyList(), itemRequestService.getRequestsWithPagination(
                thirdUser.getId(), 0, 2));
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllWithPagination(
                thirdUser.getId(), PageRequest.of(0, 2, sort));
    }
}