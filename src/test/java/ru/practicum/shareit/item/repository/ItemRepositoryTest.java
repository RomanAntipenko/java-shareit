package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    User firstUser;
    User secondUser;
    Item firstItem;
    Item secondItem;

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
    }

    @Test
    void search() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        itemRepository.save(firstItem);
        itemRepository.save(secondItem);
        List<Item> items = itemRepository.search("P", PageRequest.of(0, 2));

        Assertions.assertEquals(items.get(0), firstItem);
        assertEquals(1, items.size());
    }

    @Test
    void findAllByOwnerId() {
        User firstUserSaved = userRepository.save(firstUser);
        User secondUserSaved = userRepository.save(secondUser);
        Item firstItemSaved = itemRepository.save(firstItem);
        Item secondItemSaved = itemRepository.save(secondItem);
        List<Item> items = itemRepository.findAllByOwnerId(firstUserSaved.getId(), PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(firstItemSaved), items);
    }
}