package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(long userId, BookingDto bookingDto) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (user.isEmpty()) {
            log.debug("This user not found. In createBooking method");
            throw new UserIdNotFoundException("Такого пользователя не существует");
        }
        if (item.isEmpty()) {
            log.debug("This item not found. In createBooking method");
            throw new ItemIdNotFoundException("Такого предмета не существует");
        }
        Booking booking = BookingMapper.mapToBooking(user.get(), item.get(), bookingDto);
        if (!booking.getEnd().isAfter(booking.getStart())) {
            log.debug("Incorrect date. In createBookingMethod");
            throw new IncorrectDateException("Передана некорректная дата бронирования");
        }
        if (!booking.getItem().getAvailable()) {
            log.debug("This item is not available. In createBooking method");
            throw new ItemUnavailableException("Этот предмет недоступен для бронирования");
        }
        if (Objects.equals(booking.getItem().getOwner().getId(), booking.getBooker().getId())) {
            log.debug("Incorrect userId. In createBooking method");
            throw new ItemIdNotFoundException("Нельзя забронировать свой же предмет");
        }
        booking.setState(BookingState.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking acceptOrDeclineBooking(long userId, long bookingId, boolean approved) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            log.debug("Incorrect booking. In acceptOrDeclineBooking method");
            throw new BookingNotFoundException("Такого бронирования не существует");
        }
        if (!userRepository.existsById(userId)) {
            log.debug("This user not found. In acceptOrDeclineBooking method");
            throw new UserIdNotFoundException("Такого пользователя не существует");
        }
        if (bookingOptional.get().getState() == BookingState.APPROVED) {
            log.debug("Double available in acceptOrDeclineBooking method");
            throw new InCorrectBookingException("Невозможно подтвердить подтвержденное бронирование");
        }
        if (bookingOptional.get().getItem().getOwner().getId() != userId) {
            log.debug("Incorrect owner or double available in acceptOrDeclineBooking method");
            throw new BookingNotFoundException("Только владелец вещи может подтверждать бронирование.");
        }
        if (approved) {
            bookingOptional.get().setState(BookingState.APPROVED);
        } else {
            bookingOptional.get().setState(BookingState.REJECTED);
        }
        return bookingRepository.save(bookingOptional.get());
    }

    public Booking getBookingForOwnerOrBooker(long userId, long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            log.debug("Incorrect booking. In getBookingForOwnerOrBooker method");
            throw new BookingNotFoundException("Такого бронирования не существует");
        }
        if (!userRepository.existsById(userId)) {
            log.debug("This user not found. In getBookingForOwnerOrBooker method");
            throw new UserIdNotFoundException("Такого пользователя не существует");
        }
        if (bookingOptional.get().getItem().getOwner().getId() != userId
                && bookingOptional.get().getBooker().getId() != userId) {
            log.debug("Incorrect owner or booker in getBookingForOwnerOrBooker method");
            throw new BookingNotFoundException("Только владелец вещи или арендатор может просматривать бронирование.");
        }
        if (!bookingOptional.get().getItem().getAvailable()) {
            log.debug("This item is not available. In getBookingForOwnerOrBooker method");
            throw new ItemUnavailableException("Этот предмет недоступен для бронирования");
        }
        return bookingOptional.get();
    }

    @Override
    public Collection<Booking> getAllBookingsForUser(long userId, String state, boolean isOwner) {
        if (!userRepository.existsById(userId)) {
            log.debug("This user not found. In getAllBookingsForUser method");
            throw new UserIdNotFoundException("Такого пользователя не существует");
        }
        LocalDateTime rightNow = LocalDateTime.now();
        switch (state) {
            case "ALL":
                if (isOwner) {
                    return bookingRepository.getBookingListByOwnerId(userId);
                } else {
                    return bookingRepository.getBookingListByBookerId(userId);
                }
            case "FUTURE":
                if (isOwner) {
                    return bookingRepository.getAllFutureBookingsByOwnerId(userId, rightNow);
                } else {
                    return bookingRepository.getAllFutureBookingsByBookerId(userId, rightNow);
                }
            case "CURRENT":
                if (isOwner) {
                    return bookingRepository.getAllCurrentBookingsByOwnerId(userId, rightNow);
                } else {
                    return bookingRepository.getAllCurrentBookingsByBookerId(userId, rightNow);
                }
            case "PAST":
                if (isOwner) {
                    return bookingRepository.getAllPastBookingsByOwnerId(userId, rightNow);
                } else {
                    return bookingRepository.getAllPastBookingsByBookerId(userId, rightNow);
                }
            case "WAITING":
                if (isOwner) {
                    return bookingRepository.getAllByItemOwnerIdAndStateOrderByStartDesc(userId, BookingState.WAITING);
                } else {
                    return bookingRepository.getAllByBookerIdAndStateOrderByStartDesc(userId, BookingState.WAITING);
                }
            case "REJECTED":
                if (isOwner) {
                    return bookingRepository.getAllByItemOwnerIdAndStateOrderByStartDesc(userId, BookingState.REJECTED);
                } else {
                    return bookingRepository.getAllByBookerIdAndStateOrderByStartDesc(userId, BookingState.REJECTED);
                }
            default:
                throw new InCorrectStatusException(String.format("Unknown state: " + state));
        }
    }
}
