package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;
    User firstUser;
    User secondUser;
    Item firstItem;
    Item secondItem;
    Booking lastBooking;
    Booking nextBooking;
    Booking lastSecondBooking;
    User firstUserSaved;
    User secondUserSaved;
    Item firstItemSaved;
    Item secondItemSaved;
    Booking lastBookingSaved;
    Booking nextBookingSaved;
    Booking lastSecondBookingSaved;

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

        lastBooking = new Booking();
        lastBooking.setBooker(firstUser);
        lastBooking.setItem(secondItem);
        lastBooking.setState(BookingState.APPROVED);
        lastBooking.setStart(LocalDateTime.now().minusMonths(5));
        lastBooking.setEnd(LocalDateTime.now().minusMonths(4));

        nextBooking = new Booking();
        nextBooking.setBooker(firstUser);
        nextBooking.setItem(secondItem);
        nextBooking.setState(BookingState.APPROVED);
        nextBooking.setStart(LocalDateTime.now().plusMonths(4));
        nextBooking.setEnd(LocalDateTime.now().plusMonths(5));

        lastSecondBooking = new Booking();
        lastSecondBooking.setBooker(secondUser);
        lastSecondBooking.setItem(firstItem);
        lastSecondBooking.setState(BookingState.REJECTED);
        lastSecondBooking.setStart(LocalDateTime.now().minusMonths(5));
        lastSecondBooking.setEnd(LocalDateTime.now().minusMonths(4));

        firstUserSaved = userRepository.save(firstUser);
        secondUserSaved = userRepository.save(secondUser);
        firstItemSaved = itemRepository.save(firstItem);
        secondItemSaved = itemRepository.save(secondItem);
        lastBookingSaved = bookingRepository.save(lastBooking);
        nextBookingSaved = bookingRepository.save(nextBooking);
        lastSecondBookingSaved = bookingRepository.save(lastSecondBooking);
    }

    @Test
    void getAllCurrentBookingsByBookerId() {
        Booking currentBooking = new Booking();
        currentBooking.setBooker(firstUserSaved);
        currentBooking.setItem(secondItemSaved);
        currentBooking.setState(BookingState.APPROVED);
        currentBooking.setStart(LocalDateTime.now().minusHours(5));
        currentBooking.setEnd(LocalDateTime.now().plusHours(4));
        Booking currentBookingSaved = bookingRepository.save(currentBooking);
        List<Booking> bookings = bookingRepository.getAllCurrentBookingsByBookerId(
                firstUserSaved.getId(), LocalDateTime.now(), PageRequest.of(0, 2));

        Assertions.assertEquals(bookings, List.of(currentBookingSaved));
    }

    @Test
    void getAllCurrentBookingsByOwnerId() {
        Booking currentBooking = new Booking();
        currentBooking.setBooker(firstUserSaved);
        currentBooking.setItem(secondItemSaved);
        currentBooking.setState(BookingState.APPROVED);
        currentBooking.setStart(LocalDateTime.now().minusHours(5));
        currentBooking.setEnd(LocalDateTime.now().plusHours(4));
        Booking currentBookingSaved = bookingRepository.save(currentBooking);
        List<Booking> bookings = bookingRepository.getAllCurrentBookingsByOwnerId(
                secondUserSaved.getId(), LocalDateTime.now(), PageRequest.of(0, 2));

        Assertions.assertEquals(bookings, List.of(currentBookingSaved));
    }

    @Test
    void getAllFutureBookingsByBookerId() {
        List<Booking> bookings = bookingRepository.getAllFutureBookingsByBookerId(
                firstUserSaved.getId(), LocalDateTime.now(), PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(nextBookingSaved), bookings);
    }

    @Test
    void getAllFutureBookingsByOwnerId() {
        List<Booking> bookings = bookingRepository.getAllFutureBookingsByOwnerId(
                secondUserSaved.getId(), LocalDateTime.now(), PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(nextBookingSaved), bookings);
    }

    @Test
    void getAllPastBookingsByBookerId() {
        List<Booking> bookings = bookingRepository.getAllPastBookingsByBookerId(
                firstUserSaved.getId(), LocalDateTime.now(), PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(lastBookingSaved), bookings);
    }

    @Test
    void getAllPastBookingsByOwnerId() {
        List<Booking> bookings = bookingRepository.getAllPastBookingsByOwnerId(
                secondUserSaved.getId(), LocalDateTime.now(), PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(lastBookingSaved), bookings);
    }

    @Test
    void getAllByBookerIdAndStateOrderByStartDesc() {
        lastBookingSaved.setState(BookingState.WAITING);

        List<Booking> bookings = bookingRepository.getAllByBookerIdAndStateOrderByStartDesc(
                firstUserSaved.getId(), BookingState.WAITING, PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(lastBookingSaved), bookings);
    }

    @Test
    void getAllByItemOwnerIdAndStateOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.getAllByItemOwnerIdAndStateOrderByStartDesc(
                secondUserSaved.getId(), BookingState.APPROVED, PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(nextBookingSaved, lastBookingSaved), bookings);
    }

    @Test
    void getBookingListByOwnerId() {
        List<Booking> bookings = bookingRepository.getBookingListByOwnerId(
                secondUserSaved.getId(), PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(nextBookingSaved, lastBookingSaved), bookings);
    }

    @Test
    void getBookingListByBookerId() {
        List<Booking> bookings = bookingRepository.getBookingListByBookerId(
                firstUserSaved.getId(), PageRequest.of(0, 2));

        Assertions.assertEquals(List.of(nextBookingSaved, lastBookingSaved), bookings);
    }

    @Test
    void findFirstBookingByItemIdAndStartIsBeforeAndStateNotLikeOrderByStartDesc() {
        Booking actual = bookingRepository.findFirstBookingByItemIdAndStartIsBeforeAndStateNotLikeOrderByStartDesc(
                secondItemSaved.getId(), LocalDateTime.now(), BookingState.REJECTED);

        Assertions.assertEquals(actual, lastBookingSaved);
    }

    @Test
    void findFirstBookingByItemIdAndEndIsBeforeAndStateNotLikeOrderByEndDesc() {
        Booking currentBooking = new Booking();
        currentBooking.setBooker(firstUserSaved);
        currentBooking.setItem(secondItemSaved);
        currentBooking.setState(BookingState.APPROVED);
        currentBooking.setStart(LocalDateTime.now().minusHours(5));
        currentBooking.setEnd(LocalDateTime.now().plusHours(4));
        Booking currentBookingSaved = bookingRepository.save(currentBooking);
        Booking actual = bookingRepository.findFirstBookingByItemIdAndEndIsBeforeAndStateNotLikeOrderByEndDesc(
                secondItemSaved.getId(), LocalDateTime.now(), BookingState.REJECTED);

        Assertions.assertEquals(actual, lastBookingSaved);
    }

    @Test
    void findFirstBookingByItemIdAndStartIsAfterAndStateNotLikeOrderByStartAsc() {
        Booking actual = bookingRepository.findFirstBookingByItemIdAndStartIsAfterAndStateNotLikeOrderByStartAsc(
                secondItemSaved.getId(), LocalDateTime.now(), BookingState.REJECTED);

        Assertions.assertEquals(actual, nextBookingSaved);
    }
}