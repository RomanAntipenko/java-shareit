package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private final UserService userService;

    private final ItemService itemService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody @Valid BookingDto bookingDto) {
        return BookingMapper.mapToBookingDto(bookingService.createBooking(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrCancelBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        return BookingMapper.mapToBookingDto(bookingService.acceptOrDeclineBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingForOwnerOrBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        return BookingMapper.mapToBookingDto(bookingService.getBookingForOwnerOrBooker(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsForUser(userId, state, false).stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsForUser(userId, state, true).stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }
}
