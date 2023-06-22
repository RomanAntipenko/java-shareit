package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    User firstUser;
    User secondUser;
    Item firstItem;
    Item secondItem;
    User firstUserSaved;
    User secondUserSaved;
    Item firstItemSaved;
    Item secondItemSaved;
    ItemRequest firstItemRequest;
    ItemRequest secondItemRequest;
    ItemRequest firstItemRequestSaved;
    ItemRequest secondItemRequestSaved;

    @BeforeEach
    void init() {
        firstUser = new User();
        firstUser.setName("maks");
        firstUser.setEmail("maks220@mail.ru");

        secondUser = new User();
        secondUser.setName("sanya");
        secondUser.setEmail("gera789@mail.ru");

        firstItem = new Item();
        firstItem.setOwner(firstUser);
        firstItem.setAvailable(true);
        firstItem.setDescription("GamingPC");
        firstItem.setName("PC");

        secondItem = new Item();
        secondItem.setOwner(secondUser);
        secondItem.setAvailable(false);
        secondItem.setDescription("Beer");
        secondItem.setName("Waizen beer");

        firstUserSaved = userRepository.save(firstUser);
        secondUserSaved = userRepository.save(secondUser);
        firstItemSaved = itemRepository.save(firstItem);
        secondItemSaved = itemRepository.save(secondItem);

        firstItemRequest = new ItemRequest();
        firstItemRequest.setCreated(LocalDateTime.now());
        firstItemRequest.setRequestor(secondUserSaved);
        firstItemRequest.setDescription("I want Pc");
        firstItemRequest.setItems(new ArrayList<>());

        secondItemRequest = new ItemRequest();
        secondItemRequest.setCreated(LocalDateTime.now().minusDays(1));
        secondItemRequest.setRequestor(secondUserSaved);
        secondItemRequest.setDescription("I want perforator");
        secondItemRequest.setItems(new ArrayList<>());

        firstItemRequestSaved = itemRequestRepository.save(firstItemRequest);
        secondItemRequestSaved = itemRequestRepository.save(secondItemRequest);
    }


    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(
                secondUserSaved.getId());

        Assertions.assertEquals(itemRequests, List.of(firstItemRequestSaved, secondItemRequestSaved));
    }

    @Test
    void findAllWithPagination() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllWithPagination(
                secondUserSaved.getId(), PageRequest.of(0, 2));

        Assertions.assertEquals(Collections.emptyList(), itemRequests);
    }

    @Test
    void findAllWithPaginationWithOnlySort() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllWithPagination(
                firstUserSaved.getId(), sort);

        Assertions.assertEquals(List.of(firstItemRequestSaved, secondItemRequestSaved), itemRequests);

    }
}