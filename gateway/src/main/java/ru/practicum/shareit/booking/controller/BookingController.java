package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.exceptions.InCorrectStatusException;
import ru.practicum.shareit.booking.exceptions.IncorrectDateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingsForBookers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state", defaultValue = "all")
                                                        String stateParam,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                        Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10")
                                                        Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new InCorrectStatusException("Unknown state: " + stateParam));
        log.info("Получаем бронирование по критерию {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingDto requestDto) {
        if (!requestDto.getEnd().isAfter(requestDto.getStart())) {
            log.debug("Incorrect date");
            throw new IncorrectDateException("Передана некорректная дата бронирования. " +
                    "Начало бронирования не может быть позже его завершения");
        }
        log.info("Создаем бронирование {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получаем бронирование {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("Принимаем или отменяем бронирование {}, userId={}", bookingId, userId);

        return bookingClient.acceptOrDeclineBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "state", defaultValue = "all")
                                                     String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10")
                                                     Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new InCorrectStatusException("Unknown state: " + stateParam));
        log.info("Получаем бронирование для владельца по критерию {}, userId={}, from={}, size={}", stateParam, userId,
                from, size);
        return bookingClient.getAllBookingsForUser(userId, state, from, size);
    }
}
