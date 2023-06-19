package ru.practicum.shareit.request.exceptions;

public class PaginationNotValidException extends RuntimeException {
    public PaginationNotValidException(String message) {
        super(message);
    }
}
