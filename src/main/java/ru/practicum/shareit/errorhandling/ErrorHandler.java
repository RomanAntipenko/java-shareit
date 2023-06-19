package ru.practicum.shareit.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.InCorrectBookingException;
import ru.practicum.shareit.booking.exceptions.InCorrectStatusException;
import ru.practicum.shareit.booking.exceptions.IncorrectDateException;
import ru.practicum.shareit.item.exceptions.ItemIdNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.exceptions.OwnerIdMismatchException;
import ru.practicum.shareit.request.exceptions.PaginationNotValidException;
import ru.practicum.shareit.request.exceptions.RequestIdNotFoundException;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse emailAlreadyExistsHandler(final EmailAlreadyExistsException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userIdNotFoundHandler(final UserIdNotFoundException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse itemIdNotFoundHandler(final ItemIdNotFoundException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse ownerIdMismatchHandler(final OwnerIdMismatchException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectDateHandler(final IncorrectDateException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse itemUnavailableHandler(final ItemUnavailableException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse bookingNotFoundHandler(final BookingNotFoundException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse inCorrectBookingHandler(final InCorrectBookingException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse inCorrectStatusHandler(final InCorrectStatusException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse requestIdNotFoundHandler(final RequestIdNotFoundException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse paginationNotValidHandler(final PaginationNotValidException e) {
        log.error(e.getMessage() + ". Ошибка: " + e.getClass().getName());
        return new ErrorResponse(
                e.getMessage()
        );
    }

}
