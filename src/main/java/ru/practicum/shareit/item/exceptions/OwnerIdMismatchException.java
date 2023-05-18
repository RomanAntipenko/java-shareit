package ru.practicum.shareit.item.exceptions;

public class OwnerIdMismatchException extends RuntimeException {
    public OwnerIdMismatchException(String message) {
        super(message);
    }
}
