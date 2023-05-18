package ru.practicum.shareit.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.ItemIdNotFoundException;
import ru.practicum.shareit.item.exceptions.OwnerIdMismatchException;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse emailAlreadyExistsHandler(final EmailAlreadyExistsException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userIdNotFoundHandler(final UserIdNotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse itemIdNotFoundHandler(final ItemIdNotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse ownerIdMismatchHandler(final OwnerIdMismatchException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

}
