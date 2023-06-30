package ru.practicum.shareit.booking.exceptions;

public class IncorrectDateException extends RuntimeException {
    public IncorrectDateException(String message) {
        super(message);
    }
}
