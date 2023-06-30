package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    public BookingDto createBooking(long userId, BookingDto bookingDto);

    public BookingDto acceptOrDeclineBooking(long userId, long bookingId, boolean approved);

    public BookingDto getBookingForOwnerOrBooker(long userId, long bookingId);

    public Collection<BookingDto> getAllBookingsForUser(long userId, String state, boolean isOwner, Integer from,
                                                        Integer size);
}
