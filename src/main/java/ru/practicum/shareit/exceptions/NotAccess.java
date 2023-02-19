package ru.practicum.shareit.exceptions;

public class NotAccess extends RuntimeException {
    public NotAccess(String message) {
        super(message);
    }
}
