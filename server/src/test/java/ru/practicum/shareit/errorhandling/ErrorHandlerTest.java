package ru.practicum.shareit.errorhandling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.InCorrectBookingException;
import ru.practicum.shareit.booking.exceptions.InCorrectStatusException;
import ru.practicum.shareit.item.exceptions.ItemIdNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.request.exceptions.RequestIdNotFoundException;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;


class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void init() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void userIdNotFoundHandler() {
        var expected = new UserIdNotFoundException("User id not found");
        var actual = errorHandler.userIdNotFoundHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }

    @Test
    void itemIdNotFoundHandler() {
        var expected = new ItemIdNotFoundException("Item id not found");
        var actual = errorHandler.itemIdNotFoundHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }

    @Test
    void itemUnavailableHandler() {
        var expected = new ItemUnavailableException("Item not available for reading");
        var actual = errorHandler.itemUnavailableHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }

    @Test
    void bookingNotFoundHandler() {
        var expected = new BookingNotFoundException("Booking not found");
        var actual = errorHandler.bookingNotFoundHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }

    @Test
    void inCorrectBookingHandler() {
        var expected = new InCorrectBookingException("Booking data incorrect was given");
        var actual = errorHandler.inCorrectBookingHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }

    @Test
    void inCorrectStatusHandler() {
        var expected = new InCorrectStatusException("Incorrect status was given");
        var actual = errorHandler.inCorrectStatusHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }

    @Test
    void requestIdNotFoundHandler() {
        var expected = new RequestIdNotFoundException("ItemRequest id not found");
        var actual = errorHandler.requestIdNotFoundHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }
}