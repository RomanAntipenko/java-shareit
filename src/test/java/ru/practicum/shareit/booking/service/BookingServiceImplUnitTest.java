package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.InCorrectBookingException;
import ru.practicum.shareit.booking.exceptions.InCorrectStatusException;
import ru.practicum.shareit.booking.exceptions.IncorrectDateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exceptions.ItemIdNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exceptions.PaginationNotValidException;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    User firstUser;
    User secondUser;
    User thirdUser;
    Item firstItem;
    Item secondItem;
    Booking lastBooking;
    Booking nextBooking;
    Comment firstComment;
    Booking lastSecondBooking;
    Comment secondComment;
    Booking nextSecondBooking;

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
        lastSecondBooking.setStart(LocalDateTime.now().minusMonths(5));
        lastSecondBooking.setEnd(LocalDateTime.now().minusMonths(4));

        nextSecondBooking = new Booking();
        nextSecondBooking.setBooker(secondUser);
        nextSecondBooking.setItem(firstItem);
        nextSecondBooking.setStart(LocalDateTime.now().plusMonths(4));
        nextSecondBooking.setEnd(LocalDateTime.now().plusMonths(5));
        nextSecondBooking.setState(BookingState.WAITING);
        nextSecondBooking.setId(4L);

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
    void createBooking() {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(lastSecondBooking);
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setId(3L);
        bookingAfterSave.setEnd(lastSecondBooking.getEnd());
        bookingAfterSave.setStart(lastSecondBooking.getStart());
        bookingAfterSave.setBooker(lastSecondBooking.getBooker());
        bookingAfterSave.setItem(lastSecondBooking.getItem());
        bookingAfterSave.setState(BookingState.WAITING);

        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(itemRepository.findById(lastSecondBooking.getId()))
                .thenReturn(Optional.of(firstItem));

        lastSecondBooking.setState(BookingState.WAITING);

        Mockito.when(bookingRepository.save(lastSecondBooking))
                .thenReturn(bookingAfterSave);

        Assertions.assertEquals(bookingAfterSave, bookingService.createBooking(secondUser.getId(), bookingDto));
        Mockito.verify(bookingRepository, Mockito.times(1)).save(lastSecondBooking);
    }

    @Test
    void createBookingAndUserNotFound() {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(lastSecondBooking);
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setId(3L);
        bookingAfterSave.setEnd(lastSecondBooking.getEnd());
        bookingAfterSave.setStart(lastSecondBooking.getStart());
        bookingAfterSave.setBooker(lastSecondBooking.getBooker());
        bookingAfterSave.setItem(lastSecondBooking.getItem());
        bookingAfterSave.setState(BookingState.WAITING);

        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenThrow(UserIdNotFoundException.class);

        Assertions.assertThrows(UserIdNotFoundException.class, () -> bookingService.createBooking(
                secondUser.getId(), bookingDto));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(lastSecondBooking);
    }

    @Test
    void createBookingItemNotFound() {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(lastSecondBooking);
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setId(3L);
        bookingAfterSave.setEnd(lastSecondBooking.getEnd());
        bookingAfterSave.setStart(lastSecondBooking.getStart());
        bookingAfterSave.setBooker(lastSecondBooking.getBooker());
        bookingAfterSave.setItem(lastSecondBooking.getItem());
        bookingAfterSave.setState(BookingState.WAITING);

        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(itemRepository.findById(lastSecondBooking.getId()))
                .thenThrow(ItemIdNotFoundException.class);

        Assertions.assertThrows(ItemIdNotFoundException.class, () -> bookingService.createBooking(
                secondUser.getId(), bookingDto));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(lastSecondBooking);
    }

    @Test
    void createBookingAndIncorrectDate() {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(lastSecondBooking);
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setId(3L);
        bookingAfterSave.setEnd(lastSecondBooking.getEnd());
        bookingAfterSave.setStart(lastSecondBooking.getStart());
        bookingAfterSave.setBooker(lastSecondBooking.getBooker());
        bookingAfterSave.setItem(lastSecondBooking.getItem());
        bookingAfterSave.setState(BookingState.WAITING);
        lastBooking.setEnd(LocalDateTime.now().minusYears(2));

        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(itemRepository.findById(lastSecondBooking.getId()))
                .thenReturn(Optional.of(firstItem));

        Assertions.assertThrows(IncorrectDateException.class, () -> bookingService.createBooking(secondUser.getId(),
                BookingMapper.mapToBookingDto(lastBooking)));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void createBookingAndItemNotAvailable() {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(lastSecondBooking);
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setId(3L);
        bookingAfterSave.setEnd(lastSecondBooking.getEnd());
        bookingAfterSave.setStart(lastSecondBooking.getStart());
        bookingAfterSave.setBooker(lastSecondBooking.getBooker());
        bookingAfterSave.setItem(lastSecondBooking.getItem());
        bookingAfterSave.setState(BookingState.WAITING);
        firstItem.setAvailable(false);

        Mockito.when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito.when(itemRepository.findById(lastSecondBooking.getId()))
                .thenReturn(Optional.of(firstItem));

        Assertions.assertThrows(ItemUnavailableException.class, () -> bookingService.createBooking(secondUser.getId(),
                BookingMapper.mapToBookingDto(lastSecondBooking)));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void createBookingBookerIsOwner() {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(lastSecondBooking);
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setId(3L);
        bookingAfterSave.setEnd(lastSecondBooking.getEnd());
        bookingAfterSave.setStart(lastSecondBooking.getStart());
        bookingAfterSave.setBooker(lastSecondBooking.getBooker());
        bookingAfterSave.setItem(lastSecondBooking.getItem());
        bookingAfterSave.setState(BookingState.WAITING);

        Mockito.when(userRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito.when(itemRepository.findById(lastSecondBooking.getId()))
                .thenReturn(Optional.of(firstItem));

        Assertions.assertThrows(ItemIdNotFoundException.class, () -> bookingService.createBooking(firstUser.getId(),
                BookingMapper.mapToBookingDto(lastSecondBooking)));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void acceptOrDeclineBookingAndAccept() {
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setState(BookingState.APPROVED);
        bookingAfterSave.setId(4L);
        bookingAfterSave.setStart(nextSecondBooking.getStart());
        bookingAfterSave.setEnd(nextSecondBooking.getEnd());
        bookingAfterSave.setItem(nextSecondBooking.getItem());
        bookingAfterSave.setBooker(nextSecondBooking.getBooker());

        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.save(nextSecondBooking))
                .thenReturn(bookingAfterSave);

        Assertions.assertEquals(bookingAfterSave, bookingService.acceptOrDeclineBooking(
                firstUser.getId(), nextSecondBooking.getId(), true));
        Mockito.verify(bookingRepository, Mockito.times(1)).save(nextSecondBooking);
    }

    @Test
    void acceptOrDeclineBookingAndDecline() {
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setState(BookingState.REJECTED);
        bookingAfterSave.setId(4L);
        bookingAfterSave.setStart(nextSecondBooking.getStart());
        bookingAfterSave.setEnd(nextSecondBooking.getEnd());
        bookingAfterSave.setItem(nextSecondBooking.getItem());
        bookingAfterSave.setBooker(nextSecondBooking.getBooker());

        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.save(nextSecondBooking))
                .thenReturn(bookingAfterSave);

        Assertions.assertEquals(bookingAfterSave, bookingService.acceptOrDeclineBooking(
                firstUser.getId(), nextSecondBooking.getId(), false));
        Mockito.verify(bookingRepository, Mockito.times(1)).save(nextSecondBooking);
    }

    @Test
    void acceptOrDeclineBookingAndBookingNotFound() {
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setState(BookingState.REJECTED);
        bookingAfterSave.setId(4L);
        bookingAfterSave.setStart(nextSecondBooking.getStart());
        bookingAfterSave.setEnd(nextSecondBooking.getEnd());
        bookingAfterSave.setItem(nextSecondBooking.getItem());
        bookingAfterSave.setBooker(nextSecondBooking.getBooker());

        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenThrow(BookingNotFoundException.class);


        Assertions.assertThrows(BookingNotFoundException.class, () -> bookingService.acceptOrDeclineBooking(
                firstUser.getId(), nextSecondBooking.getId(), false));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(nextSecondBooking);
    }

    @Test
    void acceptOrDeclineBookingAndUserNotFound() {
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setState(BookingState.APPROVED);
        bookingAfterSave.setId(4L);
        bookingAfterSave.setStart(nextSecondBooking.getStart());
        bookingAfterSave.setEnd(nextSecondBooking.getEnd());
        bookingAfterSave.setItem(nextSecondBooking.getItem());
        bookingAfterSave.setBooker(nextSecondBooking.getBooker());

        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(false);

        Assertions.assertThrows(UserIdNotFoundException.class, () -> bookingService.acceptOrDeclineBooking(
                firstUser.getId(), nextSecondBooking.getId(), true));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(nextSecondBooking);
    }

    @Test
    void acceptOrDeclineBookingAndIncorrectBooking() {
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setState(BookingState.APPROVED);
        bookingAfterSave.setId(4L);
        bookingAfterSave.setStart(nextSecondBooking.getStart());
        bookingAfterSave.setEnd(nextSecondBooking.getEnd());
        bookingAfterSave.setItem(nextSecondBooking.getItem());
        bookingAfterSave.setBooker(nextSecondBooking.getBooker());
        nextSecondBooking.setState(BookingState.APPROVED);

        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);

        Assertions.assertThrows(InCorrectBookingException.class, () -> bookingService.acceptOrDeclineBooking(
                firstUser.getId(), nextSecondBooking.getId(), true));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(nextSecondBooking);
    }

    @Test
    void acceptOrDeclineBookingAndNotOwner() {
        Booking bookingAfterSave = new Booking();
        bookingAfterSave.setState(BookingState.APPROVED);
        bookingAfterSave.setId(4L);
        bookingAfterSave.setStart(nextSecondBooking.getStart());
        bookingAfterSave.setEnd(nextSecondBooking.getEnd());
        bookingAfterSave.setItem(nextSecondBooking.getItem());
        bookingAfterSave.setBooker(nextSecondBooking.getBooker());

        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(secondUser.getId()))
                .thenReturn(true);

        Assertions.assertThrows(BookingNotFoundException.class, () -> bookingService.acceptOrDeclineBooking(
                secondUser.getId(), nextSecondBooking.getId(), true));
        Mockito.verify(bookingRepository, Mockito.times(0)).save(nextSecondBooking);
    }

    @Test
    void getBookingForOwnerOrBooker() {
        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);

        Assertions.assertEquals(nextSecondBooking, bookingService.getBookingForOwnerOrBooker(
                firstUser.getId(), nextSecondBooking.getId()));
    }

    @Test
    void getBookingForOwnerOrBookerAndBookingNotFound() {
        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenThrow(BookingNotFoundException.class);

        Assertions.assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingForOwnerOrBooker(
                firstUser.getId(), nextSecondBooking.getId()));
    }

    @Test
    void getBookingForOwnerOrBookerAndUserNotFound() {
        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(false);

        Assertions.assertThrows(UserIdNotFoundException.class, () -> bookingService.getBookingForOwnerOrBooker(
                firstUser.getId(), nextSecondBooking.getId()));
    }

    @Test
    void getBookingForOwnerOrBookerAndNeitherOwnerNorBooker() {
        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(thirdUser.getId()))
                .thenReturn(true);

        Assertions.assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingForOwnerOrBooker(
                thirdUser.getId(), nextSecondBooking.getId()));
    }

    @Test
    void getBookingForOwnerOrBookerAndItemNotAvailable() {
        firstItem.setAvailable(false);
        Mockito.when(bookingRepository.findById(nextSecondBooking.getId()))
                .thenReturn(Optional.of(nextSecondBooking));
        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);

        Assertions.assertThrows(ItemUnavailableException.class, () -> bookingService.getBookingForOwnerOrBooker(
                firstUser.getId(), nextSecondBooking.getId()));
    }

    @Test
    void getAllBookingsForUserAndItsOwnerAndStateIsAll() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getBookingListByOwnerId(firstUser.getId(), PageRequest.of(0, 2)))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                firstUser.getId(), "ALL", true, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getBookingListByOwnerId(
                firstUser.getId(), PageRequest.of(0, 2));
    }

    @Test
    void getAllBookingsForUserAndItsOwnerAndStateIsFuture() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllFutureBookingsByOwnerId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                firstUser.getId(), "FUTURE", true, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllFutureBookingsByOwnerId(
                Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getAllBookingsForUserAndItsOwnerAndStateIsCurrant() {
        List<Booking> bookings = new ArrayList<>();

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllCurrentBookingsByOwnerId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                firstUser.getId(), "CURRENT", true, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllCurrentBookingsByOwnerId(
                Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getAllBookingsForUserAndItsOwnerAndStateIsPast() {
        List<Booking> bookings = new ArrayList<>();

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllPastBookingsByOwnerId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                firstUser.getId(), "PAST", true, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllPastBookingsByOwnerId(
                Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getAllBookingsForUserAndItsOwnerAndStateIsWaiting() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllByItemOwnerIdAndStateOrderByStartDesc(
                        firstUser.getId(), BookingState.WAITING, PageRequest.of(0, 2)))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                firstUser.getId(), "WAITING", true, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllByItemOwnerIdAndStateOrderByStartDesc(
                firstUser.getId(), BookingState.WAITING, PageRequest.of(0, 2));
    }

    @Test
    void getAllBookingsForUserAndItsOwnerAndStateIsRejected() {
        List<Booking> bookings = new ArrayList<>();


        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllByItemOwnerIdAndStateOrderByStartDesc(
                        firstUser.getId(), BookingState.REJECTED, PageRequest.of(0, 2)))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                firstUser.getId(), "REJECTED", true, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllByItemOwnerIdAndStateOrderByStartDesc(
                firstUser.getId(), BookingState.REJECTED, PageRequest.of(0, 2));
    }

    @Test
    void getAllBookingsForUserAndItsBookerAndStateIsAll() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(secondUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getBookingListByBookerId(secondUser.getId(), PageRequest.of(0, 2)))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                secondUser.getId(), "ALL", false, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getBookingListByBookerId(
                secondUser.getId(), PageRequest.of(0, 2));
    }

    @Test
    void getAllBookingsForUserAndItsBookerAndStateIsFuture() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(secondUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllFutureBookingsByBookerId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                secondUser.getId(), "FUTURE", false, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllFutureBookingsByBookerId(
                Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getAllBookingsForUserAndItsBookerAndStateIsCurrant() {
        List<Booking> bookings = new ArrayList<>();

        Mockito.when(userRepository.existsById(secondUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllCurrentBookingsByBookerId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                secondUser.getId(), "CURRENT", false, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllCurrentBookingsByBookerId(
                Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getAllBookingsForUserAndItsBookerAndStateIsPast() {
        List<Booking> bookings = new ArrayList<>();

        Mockito.when(userRepository.existsById(secondUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllPastBookingsByBookerId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                secondUser.getId(), "PAST", false, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllPastBookingsByBookerId(
                Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getAllBookingsForUserAndItsBookerAndStateIsWaiting() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(secondUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllByBookerIdAndStateOrderByStartDesc(
                        secondUser.getId(), BookingState.WAITING, PageRequest.of(0, 2)))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                secondUser.getId(), "WAITING", false, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllByBookerIdAndStateOrderByStartDesc(
                secondUser.getId(), BookingState.WAITING, PageRequest.of(0, 2));
    }

    @Test
    void getAllBookingsForUserAndItsBookerAndStateIsRejected() {
        List<Booking> bookings = new ArrayList<>();

        Mockito.when(userRepository.existsById(secondUser.getId()))
                .thenReturn(true);
        Mockito.when(bookingRepository.getAllByBookerIdAndStateOrderByStartDesc(
                        secondUser.getId(), BookingState.REJECTED, PageRequest.of(0, 2)))
                .thenReturn(bookings);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                secondUser.getId(), "REJECTED", false, 0, 2));
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllByBookerIdAndStateOrderByStartDesc(
                secondUser.getId(), BookingState.REJECTED, PageRequest.of(0, 2));
    }

    @Test
    void getAllBookingsForUserStateIsUnknownAndUnpaged() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);

        Assertions.assertThrows(InCorrectStatusException.class, () -> bookingService.getAllBookingsForUser(
                firstUser.getId(), "bla", true, null, null));
        Mockito.verify(bookingRepository, Mockito.times(0)).getBookingListByOwnerId(
                Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getAllBookingsForUserButWrongPagination() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(true);

        Assertions.assertThrows(PaginationNotValidException.class, () -> bookingService.getAllBookingsForUser(
                firstUser.getId(), "bla", true, -1, -1));
        Mockito.verify(bookingRepository, Mockito.times(0)).getBookingListByOwnerId(
                Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getAllBookingsForUserButUserNotFound() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(nextSecondBooking);

        Mockito.when(userRepository.existsById(firstUser.getId()))
                .thenReturn(false);

        Assertions.assertThrows(UserIdNotFoundException.class, () -> bookingService.getAllBookingsForUser(
                firstUser.getId(), "bla", true, -1, -1));
        Mockito.verify(bookingRepository, Mockito.times(0)).getBookingListByOwnerId(
                Mockito.anyLong(), Mockito.any());
    }
}