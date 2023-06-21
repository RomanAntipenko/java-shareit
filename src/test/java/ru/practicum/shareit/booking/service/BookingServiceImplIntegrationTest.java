package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    User firstUser;
    UserDto firstUserDto;
    User secondUser;
    User thirdUser;
    Item firstItem;
    BookingDto bookingDto;
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

        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMonths(5))
                .end(LocalDateTime.now().plusMonths(6))
                .build();
    }

    @Test
    void createBooking() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        firstItem.setOwner(firstSavedUser);
        Item firstSavedItem = itemRepository.save(firstItem);
        bookingDto.setItemId(firstSavedItem.getId());
        Booking booking = BookingMapper.mapToBooking(secondSavedUser, firstSavedItem, bookingDto);
        booking.setState(BookingState.WAITING);
        booking.setId(1L);
        Assertions.assertEquals(BookingMapper.mapToBookingDto(booking), bookingService.createBooking(secondSavedUser.getId(), bookingDto));
    }

    @Test
    void acceptOrDeclineBooking() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        firstItem.setOwner(firstSavedUser);
        Item firstSavedItem = itemRepository.save(firstItem);
        bookingDto.setItemId(firstSavedItem.getId());
        BookingDto booking = bookingService.createBooking(secondSavedUser.getId(), bookingDto);
        BookingDto actual = bookingService.acceptOrDeclineBooking(
                firstSavedUser.getId(), booking.getId(), true);
        booking.setStatus(BookingState.APPROVED);
        Assertions.assertEquals(booking, actual);
    }

    @Test
    void getBookingForOwnerOrBooker() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        firstItem.setOwner(firstSavedUser);
        Item firstSavedItem = itemRepository.save(firstItem);
        bookingDto.setItemId(firstSavedItem.getId());
        BookingDto booking = bookingService.createBooking(secondSavedUser.getId(), bookingDto);
        BookingDto bookingAccepted = bookingService.acceptOrDeclineBooking(
                firstSavedUser.getId(), booking.getId(), true);

        Assertions.assertEquals(
                bookingAccepted, bookingService.getBookingForOwnerOrBooker(firstSavedUser.getId(), booking.getId()));
        Assertions.assertEquals(
                bookingAccepted, bookingService.getBookingForOwnerOrBooker(secondSavedUser.getId(), booking.getId()));
    }

    @Test
    void getAllBookingsForUser() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        firstItem.setOwner(firstSavedUser);
        Item firstSavedItem = itemRepository.save(firstItem);
        bookingDto.setItemId(firstSavedItem.getId());
        BookingDto booking = bookingService.createBooking(secondSavedUser.getId(), bookingDto);
        BookingDto bookingAccepted = bookingService.acceptOrDeclineBooking(
                firstSavedUser.getId(), booking.getId(), true);
        List<BookingDto> bookings = List.of(bookingAccepted);

        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                firstUser.getId(), "ALL", true, 0, 2));
        Assertions.assertEquals(bookings, bookingService.getAllBookingsForUser(
                secondUser.getId(), "ALL", false, 0, 2));
    }
}