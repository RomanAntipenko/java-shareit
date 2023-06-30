package ru.practicum.shareit.booking.exceptions;

public class InCorrectBookingException extends RuntimeException {
    public InCorrectBookingException(String message) {
        super(message);
    }
}
