package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking mapToBooking(User user, Item item, BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        booking.setItem(item);
        return booking;
    }

    public BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .booker(BookingDtoForUser.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .item(BookingDtoForItem.builder()
                        .name(booking.getItem().getName())
                        .id(booking.getItem().getId())
                        .build())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getState())
                .id(booking.getId())
                .build();
    }

    public BookingShort mapToShortBooking(Booking booking) {
        return BookingShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
