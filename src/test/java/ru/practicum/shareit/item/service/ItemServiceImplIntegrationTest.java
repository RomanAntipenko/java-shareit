package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    User firstUser;
    UserDto firstUserDto;
    User secondUser;
    User thirdUser;
    Item firstItem;
    ItemRequest firstItemRequest;
    ItemDto firstItemDto;

    @BeforeEach
    void init() {
        firstUser = new User();
        firstUser.setName("maks");
        firstUser.setEmail("maks220@mail.ru");

        firstUserDto = UserDto.builder()
                .email(firstUser.getEmail())
                .name(firstUser.getName())
                .build();

        secondUser = new User();
        secondUser.setName("sanya");
        secondUser.setEmail("gera789@mail.ru");

        thirdUser = new User();
        thirdUser.setName("roma");
        thirdUser.setEmail("romafifa@mail.ru");

        firstItem = new Item();
        firstItem.setAvailable(true);
        firstItem.setDescription("GamingPC");
        firstItem.setName("PC");

        firstItemDto = ItemDto.builder()
                .description(firstItem.getDescription())
                .available(firstItem.getAvailable())
                .name(firstItem.getName())
                .build();

        firstItemRequest = new ItemRequest();
        firstItemRequest.setCreated(LocalDateTime.now());
        firstItemRequest.setRequestor(thirdUser);
        firstItemRequest.setDescription("I want Pc");
        firstItemRequest.setItems(new ArrayList<>());
    }

    @Test
    void createItemWithOutRequest() {
        User firstSavedUser = userRepository.save(firstUser);
        Item item = ItemMapper.mapToItem(firstSavedUser, firstItemDto);
        item.setId(1L);
        Assertions.assertEquals(ItemMapper.mapToDto(item),
                itemService.createItem(firstSavedUser.getId(), firstItemDto));
    }

    @Test
    void createItemWithRequest() {
        User firstSavedUser = userRepository.save(firstUser);
        User thirdSavedUser = userRepository.save(thirdUser);
        ItemRequest firstSavedItemRequest = itemRequestRepository.save(firstItemRequest);
        firstItemDto.setRequestId(1L);
        Item item = ItemMapper.mapToItem(firstSavedUser, firstItemDto);
        item.setRequest(firstSavedItemRequest);
        firstSavedItemRequest.getItems().add(item);
        itemRequestRepository.save(firstSavedItemRequest);
        item.setId(1L);

        Assertions.assertEquals(ItemMapper.mapToDto(item),
                itemService.createItem(firstSavedUser.getId(), firstItemDto));
    }

    @Test
    void updateItem() {
        User firstSavedUser = userRepository.save(firstUser);
        ItemDto firstSavedItem = itemService.createItem(firstSavedUser.getId(), firstItemDto);
        ItemDto update = ItemDto.builder()
                .name("mouse")
                .description("now its mouse")
                .available(false)
                .build();
        Item item = ItemMapper.mapToItem(firstSavedItem.getId(), firstUser, update);
        item.setName(update.getName());
        item.setDescription(update.getDescription());
        item.setAvailable(update.getAvailable());
        item.setId(1L);
        Assertions.assertEquals(ItemMapper.mapToDto(item), itemService.updateItem(firstSavedUser.getId(), firstSavedItem.getId(), update));
    }

    @Test
    void getAllItemsByOwnerWithPagination() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        ItemDto secondItemDto = ItemDto.builder()
                .description("Just beer for casual drinking")
                .name("Beer")
                .available(true)
                .build();
        ItemDto firstSavedItemDto = itemService.createItem(firstSavedUser.getId(), firstItemDto);
        Item secondSavedItem = itemRepository.save(ItemMapper.mapToItem(secondSavedUser, secondItemDto));

        List<ItemDto> items = List.of(secondSavedItem).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMonths(5))
                .end(LocalDateTime.now().plusMonths(6))
                .build();
        Booking booking = BookingMapper.mapToBooking(firstSavedUser, secondSavedItem, bookingDto);
        booking.setState(BookingState.APPROVED);
        Booking bookingAfterSaveAndApproved = bookingRepository.save(booking);
        items.get(0).setNextBooking(BookingMapper.mapToShortBooking(bookingAfterSaveAndApproved));

        Assertions.assertEquals(items, itemService.getAllItemsByOwner(secondSavedUser.getId(), 0, 2));
    }

    @Test
    void getItemById() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        ItemDto secondItemDto = ItemDto.builder()
                .description("Just beer for casual drinking")
                .name("Beer")
                .available(true)
                .build();
        ItemDto firstSavedItemDto = itemService.createItem(firstSavedUser.getId(), firstItemDto);
        Item secondSavedItem = itemRepository.save(ItemMapper.mapToItem(secondSavedUser, secondItemDto));

        ItemDto secondSavedItemDto = ItemMapper.mapToDto(secondSavedItem);
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().minusMonths(5))
                .end(LocalDateTime.now().minusMonths(4))
                .build();
        Booking booking = BookingMapper.mapToBooking(firstSavedUser, secondSavedItem, bookingDto);
        booking.setState(BookingState.APPROVED);
        Booking bookingAfterSaveAndApproved = bookingRepository.save(booking);
        secondSavedItemDto.setLastBooking(BookingMapper.mapToShortBooking(bookingAfterSaveAndApproved));
        CommentDto commentDto = CommentDto.builder()
                .text("obviously like")
                .build();
        Comment comment = CommentMapper.mapToComment(firstSavedUser, secondSavedItem, commentDto, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        secondSavedItemDto.setComments(List.of(CommentMapper.mapToCommentDto(savedComment)));
        Assertions.assertEquals(secondSavedItemDto, itemService.getItemById(
                secondSavedUser.getId(), secondSavedItem.getId()));
    }

    @Test
    void searchItem() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        ItemDto secondItemDto = ItemDto.builder()
                .description("Just beer for casual drinking")
                .name("Beer")
                .available(true)
                .build();
        ItemDto firstSavedItemDto = itemService.createItem(firstSavedUser.getId(), firstItemDto);
        Item secondSavedItem = itemRepository.save(ItemMapper.mapToItem(secondSavedUser, secondItemDto));
        String text = "Bee";
        List<ItemDto> items = List.of(ItemMapper.mapToDto(secondSavedItem));
        Assertions.assertEquals(items, itemService.searchItem(text, 0, 2));
    }

    @Test
    void postComment() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        ItemDto secondItemDto = ItemDto.builder()
                .description("Just beer for casual drinking")
                .name("Beer")
                .available(true)
                .build();
        ItemDto firstSavedItemDto = itemService.createItem(firstSavedUser.getId(), firstItemDto);

        Item secondSavedItem = itemRepository.save(ItemMapper.mapToItem(secondSavedUser, secondItemDto));

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().minusMonths(5))
                .end(LocalDateTime.now().minusMonths(4))
                .build();
        Booking booking = BookingMapper.mapToBooking(firstSavedUser, secondSavedItem, bookingDto);
        booking.setState(BookingState.APPROVED);
        Booking bookingAfterSaveAndApproved = bookingRepository.save(booking);
        CommentDto commentDto = CommentDto.builder()
                .text("obviously like")
                .build();
        Comment comment = CommentMapper.mapToComment(firstSavedUser, secondSavedItem, commentDto, LocalDateTime.now());
        comment.setId(1L);
        CommentDto actual = itemService.postComment(
                firstSavedUser.getId(), secondSavedItem.getId(), commentDto);

        Assertions.assertEquals(comment.getId(), actual.getId());
        Assertions.assertEquals(comment.getText(), actual.getText());
        Assertions.assertEquals(comment.getAuthor().getName(), actual.getAuthorName());
        Assertions.assertEquals(comment.getCreated().truncatedTo(ChronoUnit.MINUTES),
                actual.getCreated().truncatedTo(ChronoUnit.MINUTES));
    }
}