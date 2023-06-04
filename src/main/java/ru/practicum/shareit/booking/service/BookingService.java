package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    public Booking createBooking(long userId, BookingDto bookingDto);

    public Booking acceptOrDeclineBooking(long userId, long bookingId, boolean approved);

    public Booking getBookingForOwnerOrBooker(long userId, long bookingId);

    public Collection<Booking> getAllBookingsForUser(long userId, String state, boolean isOwner);
}
