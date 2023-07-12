package ru.practicum.shareit.errorhandling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.exceptions.InCorrectStatusException;
import ru.practicum.shareit.booking.exceptions.IncorrectDateException;


class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void init() {
        errorHandler = new ErrorHandler();
    }


    @Test
    void incorrectDateHandler() {
        var expected = new IncorrectDateException("Incorrect date was given");
        var actual = errorHandler.incorrectDateHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }

    /*@Test
    void itemUnavailableHandler() {
        var expected = new ItemUnavailableException("Item not available for reading");
        var actual = errorHandler.itemUnavailableHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }*/


    /*@Test
    void inCorrectBookingHandler() {
        var expected = new InCorrectBookingException("Booking data incorrect was given");
        var actual = errorHandler.inCorrectBookingHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }*/

    @Test
    void inCorrectStatusHandler() {
        var expected = new InCorrectStatusException("Incorrect status was given");
        var actual = errorHandler.inCorrectStatusHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }


    /*@Test
    void paginationNotValidHandler() {
        var expected = new PaginationNotValidException("Pagination data incorrect was given");
        var actual = errorHandler.paginationNotValidHandler(expected);
        Assertions.assertNotNull(expected);
        Assertions.assertEquals(expected.getMessage(), actual.getError());
    }*/
}