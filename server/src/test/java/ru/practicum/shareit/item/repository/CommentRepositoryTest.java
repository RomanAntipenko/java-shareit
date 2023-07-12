package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;
    User firstUser;
    User secondUser;
    Item firstItem;
    Item secondItem;
    Comment comment;

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

        comment = new Comment();
        comment.setAuthor(firstUser);
        comment.setItem(secondItem);
        comment.setCreated(LocalDateTime.now());
        comment.setText("Super");
    }

    @Test
    void findAllByItemId() {
        User firstUserSaved = userRepository.save(firstUser);
        User secondUserSaved = userRepository.save(secondUser);
        Item firstItemSaved = itemRepository.save(firstItem);
        Item secondItemSaved = itemRepository.save(secondItem);
        Comment savedComment = commentRepository.save(comment);

        Assertions.assertEquals(List.of(savedComment), commentRepository.findAllByItemId(secondItemSaved.getId()));
    }
}