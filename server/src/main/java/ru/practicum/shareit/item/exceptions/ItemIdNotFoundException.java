package ru.practicum.shareit.item.exceptions;

public class ItemIdNotFoundException extends RuntimeException {
    public ItemIdNotFoundException(String message) {
        super(message);
    }
}
