package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.exceptions.PaginationNotValidException;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("Такого пользователя не существует"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemIdNotFoundException("Такого предмета не существует"));
        Booking booking = BookingMapper.mapToBooking(user, item, bookingDto);
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
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto acceptOrDeclineBooking(long userId, long bookingId, boolean approved) {
        Booking bookingOptional = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Такого бронирования не существует"));
        if (!userRepository.existsById(userId)) {
            log.debug("This user not found. In acceptOrDeclineBooking method");
            throw new UserIdNotFoundException("Такого пользователя не существует");
        }
        if (bookingOptional.getState() == BookingState.APPROVED) {
            log.debug("Double available in acceptOrDeclineBooking method");
            throw new InCorrectBookingException("Невозможно подтвердить подтвержденное бронирование");
        }
        if (bookingOptional.getItem().getOwner().getId() != userId) {
            log.debug("Incorrect owner or double available in acceptOrDeclineBooking method");
            throw new BookingNotFoundException("Только владелец вещи может подтверждать бронирование.");
        }
        if (approved) {
            bookingOptional.setState(BookingState.APPROVED);
        } else {
            bookingOptional.setState(BookingState.REJECTED);
        }
        return BookingMapper.mapToBookingDto(bookingRepository.save(bookingOptional));
    }

    public BookingDto getBookingForOwnerOrBooker(long userId, long bookingId) {
        Booking bookingOptional = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Такого бронирования не существует"));
        if (!userRepository.existsById(userId)) {
            log.debug("This user not found. In getBookingForOwnerOrBooker method");
            throw new UserIdNotFoundException("Такого пользователя не существует");
        }
        if (bookingOptional.getItem().getOwner().getId() != userId
                && bookingOptional.getBooker().getId() != userId) {
            log.debug("Incorrect owner or booker in getBookingForOwnerOrBooker method");
            throw new BookingNotFoundException("Только владелец вещи или арендатор может просматривать бронирование.");
        }
        if (!bookingOptional.getItem().getAvailable()) {
            log.debug("This item is not available. In getBookingForOwnerOrBooker method");
            throw new ItemUnavailableException("Этот предмет недоступен для бронирования");
        }
        return BookingMapper.mapToBookingDto(bookingOptional);
    }

    @Override
    public Collection<BookingDto> getAllBookingsForUser(long userId, String state, boolean isOwner, Integer from,
                                                        Integer size) {
        if (!userRepository.existsById(userId)) {
            log.debug("This user not found. In getAllBookingsForUser method");
            throw new UserIdNotFoundException("Такого пользователя не существует");
        }
        LocalDateTime rightNow = LocalDateTime.now();
        Pageable pageable;
        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new PaginationNotValidException("Переданы некорректные данные для пагинации");
            }
            int page = from / size;
            pageable = PageRequest.of(page, size);
        } else {
            pageable = Pageable.unpaged();
        }
        switch (state) {
            case "ALL":
                if (isOwner) {
                    return bookingRepository.getBookingListByOwnerId(userId, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.getBookingListByBookerId(userId, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                }
            case "FUTURE":
                if (isOwner) {
                    return bookingRepository.getAllFutureBookingsByOwnerId(userId, rightNow, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.getAllFutureBookingsByBookerId(userId, rightNow, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                }
            case "CURRENT":
                if (isOwner) {
                    return bookingRepository.getAllCurrentBookingsByOwnerId(userId, rightNow, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.getAllCurrentBookingsByBookerId(userId, rightNow, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                }
            case "PAST":
                if (isOwner) {
                    return bookingRepository.getAllPastBookingsByOwnerId(userId, rightNow, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.getAllPastBookingsByBookerId(userId, rightNow, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                }
            case "WAITING":
                if (isOwner) {
                    return bookingRepository.getAllByItemOwnerIdAndStateOrderByStartDesc(
                                    userId, BookingState.WAITING, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.getAllByBookerIdAndStateOrderByStartDesc(
                                    userId, BookingState.WAITING, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                }
            case "REJECTED":
                if (isOwner) {
                    return bookingRepository.getAllByItemOwnerIdAndStateOrderByStartDesc(
                                    userId, BookingState.REJECTED, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.getAllByBookerIdAndStateOrderByStartDesc(
                                    userId, BookingState.REJECTED, pageable).stream()
                            .map(BookingMapper::mapToBookingDto)
                            .collect(Collectors.toList());
                }
            default:
                throw new InCorrectStatusException(String.format("Unknown state: " + state));
        }
    }
}
