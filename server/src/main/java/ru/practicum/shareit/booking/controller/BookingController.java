package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody @Valid BookingDto bookingDto) {
        log.info("Вызван метод создания бронирования, в BookingController");
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrCancelBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        log.info("Вызван метод подтверждения или отмены бронирования, в BookingController");
        return bookingService.acceptOrDeclineBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingForOwnerOrBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        log.info("Вызван метод просмотра бронирования владельцем или клиентом, в BookingController");
        return bookingService.getBookingForOwnerOrBooker(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsForBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(required = false) Integer from,
                                                          @RequestParam(required = false) Integer size) {
        log.info("Вызван метод просмотра списка бронирования для клиента, в BookingController");
        return bookingService.getAllBookingsForUser(userId, state, false, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size) {
        log.info("Вызван метод просмотра списка бронирования для владельца, в BookingController");
        return bookingService.getAllBookingsForUser(userId, state, true, from, size);
    }
}
