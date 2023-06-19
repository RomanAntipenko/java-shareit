package ru.practicum.shareit.request.exceptions;

public class RequestIdNotFoundException extends RuntimeException {
    public RequestIdNotFoundException(String message) {
        super(message);
    }
}
