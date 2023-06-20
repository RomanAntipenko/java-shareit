package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.exceptions.InCorrectBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.ItemIdNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exceptions.PaginationNotValidException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    BookingRepository bookingRepository;
    @Captor
    ArgumentCaptor<Item> argumentCaptor;
    User firstUser;
    User secondUser;
    User thirdUser;
    Item firstItem;
    Item secondItem;
    Booking lastBooking;
    Booking nextBooking;
    ItemRequest firstItemRequest;
    Comment firstComment;
    Booking lastSecondBooking;
    Comment secondComment;

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

        secondItem = new Item();
        secondItem.setOwner(secondUser);
        secondItem.setAvailable(false);
        secondItem.setDescription("Beer");
        secondItem.setName("Waizen beer");

        firstItemRequest = new ItemRequest();
        firstItemRequest.setCreated(LocalDateTime.now());
        firstItemRequest.setRequestor(thirdUser);
        firstItemRequest.setDescription("I want Pc");
        firstItemRequest.setId(1L);
        firstItemRequest.setItems(new ArrayList<>());

        lastBooking = new Booking();
        lastBooking.setBooker(thirdUser);
        lastBooking.setItem(secondItem);
        lastBooking.setState(BookingState.APPROVED);
        lastBooking.setStart(LocalDateTime.now().minusMonths(5));
        lastBooking.setEnd(LocalDateTime.now().minusMonths(4));
        lastBooking.setId(1L);

        nextBooking = new Booking();
        nextBooking.setBooker(thirdUser);
        nextBooking.setItem(secondItem);
        nextBooking.setState(BookingState.APPROVED);
        nextBooking.setStart(LocalDateTime.now().plusMonths(4));
        nextBooking.setEnd(LocalDateTime.now().plusMonths(5));
        nextBooking.setId(2L);

        lastSecondBooking = new Booking();
        lastSecondBooking.setBooker(secondUser);
        lastSecondBooking.setItem(firstItem);
        lastSecondBooking.setState(BookingState.APPROVED);
        lastSecondBooking.setStart(LocalDateTime.now().minusMonths(5));
        lastSecondBooking.setEnd(LocalDateTime.now().minusMonths(4));
        lastSecondBooking.setId(3L);

        firstComment = new Comment();
        firstComment.setItem(firstItem);
        firstComment.setText("like");
        firstComment.setCreated(LocalDateTime.now());
        firstComment.setId(1L);
        firstComment.setAuthor(secondUser);

        secondComment = new Comment();
        secondComment.setItem(firstItem);
        secondComment.setText("like");
        secondComment.setCreated(LocalDateTime.now());
        secondComment.setAuthor(secondUser);
    }

    @Test
    void createItemAndItsOkWithoutRequest() {
        ItemDto itemDto = ItemDto.builder()
                .name(firstItem.getName())
                .available(firstItem.getAvailable())
                .description(firstItem.getDescription())
                .build();
        Item itemAfterSave = new Item();
        itemAfterSave.setName(itemDto.getName());
        itemAfterSave.setDescription(itemDto.getDescription());
        itemAfterSave.setAvailable(itemDto.getAvailable());
        itemAfterSave.setId(1L);
        itemAfterSave.setOwner(firstUser);

        Mockito.when(userRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito.when(itemRepository.save(firstItem))
                .thenReturn(itemAfterSave);

        Item actual = itemService.createItem(firstUser.getId(), itemDto);

        Mockito.verify(itemRepository, Mockito.times(1)).save(firstItem);
        Assertions.assertEquals(actual, itemAfterSave);
    }

    @Test
    void createItemAndUserNotFoundWithoutRequest() {
        ItemDto itemDto = ItemDto.builder()
                .name(firstItem.getName())
                .available(firstItem.getAvailable())
                .description(firstItem.getDescription())
                .build();
        Item itemAfterSave = new Item();
        itemAfterSave.setName(itemDto.getName());
        itemAfterSave.setDescription(itemDto.getDescription());
        itemAfterSave.setAvailable(itemDto.getAvailable());
        itemAfterSave.setId(1L);
        itemAfterSave.setOwner(firstUser);

        Mockito.when(userRepository.findById(firstUser.getId()))
                .thenThrow(UserIdNotFoundException.class);

        Mockito.verify(itemRepository, Mockito.times(0)).save(itemAfterSave);
        Assertions.assertThrows(UserIdNotFoundException.class, () -> itemService.createItem(firstUser.getId(), itemDto));
    }

    @Test
    void createItemAndItsOkWithRequest() {
        ItemDto itemDto = ItemDto.builder()
                .name(firstItem.getName())
                .available(firstItem.getAvailable())
                .description(firstItem.getDescription())
                .requestId(firstItemRequest.getId())
                .build();
        Item itemAfterSave = new Item();
        itemAfterSave.setName(itemDto.getName());
        itemAfterSave.setDescription(itemDto.getDescription());
        itemAfterSave.setAvailable(itemDto.getAvailable());
        itemAfterSave.setId(1L);
        itemAfterSave.setOwner(firstUser);
        itemAfterSave.setRequest(firstItemRequest);

        Mockito.when(userRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito.when(itemRequestRepository.existsById(itemDto.getRequestId()))
                .thenReturn(true);
        Mockito.when(itemRequestRepository.findById(firstItemRequest.getId()))
                .thenReturn(Optional.of(firstItemRequest));

        firstItem.setRequest(firstItemRequest);
        firstItemRequest.getItems().add(firstItem);
        itemRequestRepository.save(firstItemRequest);

        Mockito.when(itemRepository.save(firstItem))
                .thenReturn(itemAfterSave);

        Item actual = itemService.createItem(firstUser.getId(), itemDto);

        Mockito.verify(itemRepository, Mockito.times(1)).save(firstItem);
        Assertions.assertEquals(actual, itemAfterSave);
    }

    @Test
    void updateItemAndItsOk() {
        ItemDto itemDto = ItemDto.builder()
                .name("NotPcGaming")
                .build();
        firstItem.setId(1L);

        Mockito.when(userRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenReturn(Optional.of(firstItem));

        Item actual = itemService.updateItem(firstUser.getId(), firstItem.getId(), itemDto);

        Mockito.verify(itemRepository).save(argumentCaptor.capture());
        Mockito.verify(itemRepository, Mockito.times(1)).save(argumentCaptor.capture());

        Item expected = argumentCaptor.getValue();
        Assertions.assertEquals(expected.getAvailable(), firstItem.getAvailable());
        Assertions.assertEquals(expected.getName(), itemDto.getName());
        Assertions.assertEquals(expected.getDescription(), firstItem.getDescription());
    }

    @Test
    void updateItemAndUserNotFound() {
        ItemDto itemDto = ItemDto.builder()
                .name("NotPcGaming")
                .build();
        firstItem.setId(1L);

        Mockito.when(userRepository.findById(firstUser.getId()))
                .thenThrow(UserIdNotFoundException.class);

        Assertions.assertThrows(UserIdNotFoundException.class, () -> itemService.updateItem(
                firstUser.getId(), firstItem.getId(), itemDto));
        Mockito.verify(itemRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateItemAndItemNotFound() {
        ItemDto itemDto = ItemDto.builder()
                .name("NotPcGaming")
                .build();
        firstItem.setId(1L);

        Mockito.when(userRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenThrow(ItemIdNotFoundException.class);

        Assertions.assertThrows(ItemIdNotFoundException.class, () -> itemService.updateItem(
                firstUser.getId(), firstItem.getId(), itemDto));

        Mockito.verify(itemRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void getAllItemsByOwnerWithoutPagination() {
        firstItem.setId(1L);
        List<Item> items = new ArrayList<>();
        items.add(firstItem);

        Mockito.when(userRepository.existsById(1L))
                .thenReturn(true);
        Mockito.when(itemRepository.findAllByOwnerId(firstItem.getOwner().getId(), Pageable.unpaged()))
                .thenReturn(items);

        List<ItemDto> itemsDto = items.stream().map(ItemMapper::mapToDto).collect(Collectors.toList());
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndStartIsAfterAndStateNotLikeOrderByStartAsc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBooking);
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndStartIsBeforeAndStateNotLikeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBooking);
        itemsDto.get(0).setLastBooking(BookingMapper.mapToShortBooking(lastBooking));
        itemsDto.get(0).setNextBooking(BookingMapper.mapToShortBooking(nextBooking));

        Collection<ItemDto> actual = itemService.getAllItemsByOwner(firstUser.getId(), null, null);
        Assertions.assertEquals(actual, itemsDto);
        Mockito.verify(itemRepository,
                Mockito.times(2)).findAllByOwnerId(1L, Pageable.unpaged());
    }

    @Test
    void getAllItemsByOwnerWithPagination() {
        firstItem.setId(1L);
        List<Item> items = new ArrayList<>();
        items.add(firstItem);

        Mockito.when(userRepository.existsById(1L))
                .thenReturn(true);
        Mockito.when(itemRepository.findAllByOwnerId(firstItem.getOwner().getId(), PageRequest.of(0, 2)))
                .thenReturn(items);

        List<ItemDto> itemsDto = items.stream().map(ItemMapper::mapToDto).collect(Collectors.toList());
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndStartIsAfterAndStateNotLikeOrderByStartAsc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBooking);
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndStartIsBeforeAndStateNotLikeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBooking);
        itemsDto.get(0).setLastBooking(BookingMapper.mapToShortBooking(lastBooking));
        itemsDto.get(0).setNextBooking(BookingMapper.mapToShortBooking(nextBooking));

        Collection<ItemDto> actual = itemService.getAllItemsByOwner(firstUser.getId(), 0, 2);
        Assertions.assertEquals(actual, itemsDto);
        Mockito.verify(itemRepository,
                Mockito.times(1)).findAllByOwnerId(1L, PageRequest.of(0, 2));
    }

    @Test
    void getAllItemsByOwnerWithPaginationButItsWrong() {
        firstItem.setId(1L);
        List<Item> items = new ArrayList<>();
        items.add(firstItem);

        Mockito.when(userRepository.existsById(1L))
                .thenReturn(true);

        Assertions.assertThrows(PaginationNotValidException.class, () -> itemService.getAllItemsByOwner(
                1, -1, -1));
        Mockito.verify(itemRepository,
                Mockito.times(0)).findAllByOwnerId(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getAllItemsByOwnerAndOwnerNotFound() {
        firstItem.setId(1L);
        List<Item> items = new ArrayList<>();
        items.add(firstItem);

        Mockito.when(userRepository.existsById(1L))
                .thenThrow(UserIdNotFoundException.class);
        Assertions.assertThrows(UserIdNotFoundException.class,
                () -> itemService.getAllItemsByOwner(1L, null, null));
    }

    @Test
    void getItemByIdAndItsOkWithOutBooking() {
        firstItem.setId(1L);
        List<Comment> comments = new ArrayList<>();
        comments.add(firstComment);
        ItemDto itemDto = ItemMapper.mapToDto(firstItem);
        itemDto.setComments(comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList()));

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenReturn(Optional.of(firstItem));
        Mockito.when(commentRepository.findAllByItemId(firstItem.getId()))
                .thenReturn(comments);

        Assertions.assertEquals(itemDto, itemService.getItemById(firstUser.getId(), firstItem.getId()));
    }

    @Test
    void getItemByIdAndItsOkWithBooking() {
        firstItem.setId(1L);
        List<Comment> comments = new ArrayList<>();
        comments.add(firstComment);
        ItemDto itemDto = ItemMapper.mapToDto(firstItem);
        itemDto.setComments(comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList()));
        itemDto.setLastBooking(BookingMapper.mapToShortBooking(lastBooking));
        itemDto.setNextBooking(BookingMapper.mapToShortBooking(nextBooking));

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenReturn(Optional.of(firstItem));
        Mockito.when(commentRepository.findAllByItemId(firstItem.getId()))
                .thenReturn(comments);
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndStartIsAfterAndStateNotLikeOrderByStartAsc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBooking);
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndStartIsBeforeAndStateNotLikeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBooking);

        Assertions.assertEquals(itemDto, itemService.getItemById(firstUser.getId(), firstItem.getId()));
    }

    @Test
    void getItemByIdAndItsUserIdNotFound() {
        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(false);

        Assertions.assertThrows(UserIdNotFoundException.class, () -> itemService.getItemById(
                firstUser.getId(), 1L));
        Mockito.verify(itemRepository, Mockito.times(0)).findById(firstUser.getId());
        Mockito.verify(commentRepository, Mockito.times(0)).findAllByItemId(Mockito.anyLong());
    }

    @Test
    void getItemByIdAndItsItemNotFound() {
        firstItem.setId(1L);

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenThrow(ItemIdNotFoundException.class);

        Assertions.assertThrows(ItemIdNotFoundException.class, () -> itemService.getItemById(
                firstUser.getId(), firstItem.getId()));
        Mockito.verify(commentRepository, Mockito.times(0)).findAllByItemId(firstItem.getId());
    }

    @Test
    void searchItemAndItsOk() {
        List<Item> items = new ArrayList<>();
        items.add(secondItem);
        String text = "Beer";
        Mockito.when(itemRepository.search(Mockito.anyString(), Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    String text1 = invocationOnMock.getArgument(0, String.class);
                    if (text1.equals("Beer")) {
                        return items;
                    } else if (text1.isEmpty()) {
                        return Collections.emptyList();
                    }
                    return null;
                });
        Assertions.assertEquals(items, itemService.searchItem(text, null, null));
        Assertions.assertEquals(Collections.emptyList(), itemService.searchItem("", null, null));
    }

    @Test
    void searchItemAndItsWrongPagination() {
        List<Item> items = new ArrayList<>();
        items.add(secondItem);
        String text = "Beer";

        Assertions.assertThrows(PaginationNotValidException.class, () -> itemService.searchItem(
                text, -1, -1));

        Mockito.verify(itemRepository, Mockito.times(0)).search(
                Mockito.anyString(), Mockito.any());
    }

    @Test
    void postComment() {
        firstItem.setId(1L);
        CommentDto commentDto = CommentMapper.mapToCommentDto(secondComment);
        Comment savedComment = new Comment();
        savedComment.setItem(secondComment.getItem());
        savedComment.setText(secondComment.getText());
        savedComment.setCreated(secondComment.getCreated());
        savedComment.setAuthor(secondComment.getAuthor());
        savedComment.setId(2L);
        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenReturn(Optional.of(firstItem));
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndEndIsBeforeAndStateNotLikeOrderByEndDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(lastSecondBooking);
        Mockito.when(commentRepository.save(Mockito.any()))
                .thenReturn(savedComment);

        Assertions.assertEquals(savedComment, itemService.postComment(
                2L, 1L, commentDto));
        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void postCommentAndItsIncorrectBookingByTime() {
        firstItem.setId(1L);
        CommentDto commentDto = CommentMapper.mapToCommentDto(secondComment);
        Comment savedComment = new Comment();
        savedComment.setItem(secondComment.getItem());
        savedComment.setText(secondComment.getText());
        savedComment.setCreated(secondComment.getCreated());
        savedComment.setAuthor(secondComment.getAuthor());
        savedComment.setId(2L);
        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenReturn(Optional.of(firstItem));
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndEndIsBeforeAndStateNotLikeOrderByEndDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(null);
        Assertions.assertThrows(InCorrectBookingException.class, () -> itemService.postComment(
                2L, 1L, commentDto));
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void postCommentAndItsIncorrectBookingByWrongUser() {
        firstItem.setId(1L);
        CommentDto commentDto = CommentMapper.mapToCommentDto(secondComment);
        Comment savedComment = new Comment();
        savedComment.setItem(secondComment.getItem());
        savedComment.setText(secondComment.getText());
        savedComment.setCreated(secondComment.getCreated());
        savedComment.setAuthor(secondComment.getAuthor());
        savedComment.setId(2L);
        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenReturn(Optional.of(firstItem));
        Mockito.when(bookingRepository.findFirstBookingByItemIdAndEndIsBeforeAndStateNotLikeOrderByEndDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBooking);
        Assertions.assertThrows(InCorrectBookingException.class, () -> itemService.postComment(
                2L, 1L, commentDto));
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void postCommentAndItemNotFound() {
        firstItem.setId(1L);
        CommentDto commentDto = CommentMapper.mapToCommentDto(secondComment);
        Comment savedComment = new Comment();
        savedComment.setItem(secondComment.getItem());
        savedComment.setText(secondComment.getText());
        savedComment.setCreated(secondComment.getCreated());
        savedComment.setAuthor(secondComment.getAuthor());
        savedComment.setId(2L);
        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(itemRepository.findById(firstItem.getId()))
                .thenThrow(ItemIdNotFoundException.class);
        Assertions.assertThrows(ItemIdNotFoundException.class, () -> itemService.postComment(
                2L, 1L, commentDto));
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void postCommentAndUserNotFound() {
        firstItem.setId(1L);
        CommentDto commentDto = CommentMapper.mapToCommentDto(secondComment);
        Comment savedComment = new Comment();
        savedComment.setItem(secondComment.getItem());
        savedComment.setText(secondComment.getText());
        savedComment.setCreated(secondComment.getCreated());
        savedComment.setAuthor(secondComment.getAuthor());
        savedComment.setId(2L);
        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenThrow(UserIdNotFoundException.class);
        Assertions.assertThrows(UserIdNotFoundException.class, () -> itemService.postComment(
                2L, 1L, commentDto));
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any());
    }
}