package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.exceptions.RequestIdNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplIntegrationTest {

    private final UserRepository userRepository;
    private final ItemRequestService itemRequestService;

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
    void createRequest() {
        User firstSavedUser = userRepository.save(firstUser);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(
                firstSavedUser, Collections.emptyList(), LocalDateTime.now(), firstItemRequestDto);
        itemRequest.setId(1L);

        Assertions.assertEquals(itemRequest.getId(), itemRequestService.createRequest(firstSavedUser.getId(),
                firstItemRequestDto).getId());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestService.createRequest(firstSavedUser.getId(),
                firstItemRequestDto).getDescription());
        Assertions.assertEquals(itemRequest.getCreated().truncatedTo(ChronoUnit.MINUTES), itemRequestService.createRequest(firstSavedUser.getId(),
                firstItemRequestDto).getCreated().truncatedTo(ChronoUnit.MINUTES));
    }

    @Test
    void getRequestsByRequestor() {
        User firstSavedUser = userRepository.save(firstUser);
        ItemRequestDto itemRequest = itemRequestService.createRequest(firstSavedUser.getId(), firstItemRequestDto);
        List<ItemRequestDto> list = List.of(itemRequest);

        Assertions.assertEquals(list, itemRequestService.getRequestsByRequestor(firstSavedUser.getId()));
    }

    @Test
    void getRequestByRequestId() {
        User firstSavedUser = userRepository.save(firstUser);
        ItemRequestDto itemRequest = itemRequestService.createRequest(firstSavedUser.getId(), firstItemRequestDto);

        Assertions.assertEquals(itemRequest,
                itemRequestService.getRequestByRequestId(firstSavedUser.getId(), itemRequest.getId()));
        Assertions.assertThrows(RequestIdNotFoundException.class,
                () -> itemRequestService.getRequestByRequestId(firstSavedUser.getId(), 99L));
    }

    @Test
    void getRequestsWithPagination() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        ItemRequestDto itemRequest = itemRequestService.createRequest(firstSavedUser.getId(), firstItemRequestDto);
        List<ItemRequestDto> list = List.of(itemRequest);

        Assertions.assertEquals(Collections.emptyList(),
                itemRequestService.getRequestsWithPagination(firstSavedUser.getId(), 0, 2));
        Assertions.assertEquals(list, itemRequestService.getRequestsWithPagination(
                secondSavedUser.getId(), 0, 2));
    }
}