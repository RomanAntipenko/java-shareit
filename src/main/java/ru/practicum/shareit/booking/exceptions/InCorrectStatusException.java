package ru.practicum.shareit.booking.exceptions;

public class InCorrectStatusException extends RuntimeException {
    public InCorrectStatusException(String message) {
        super(message);
    }
}
