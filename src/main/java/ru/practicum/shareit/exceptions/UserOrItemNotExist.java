package ru.practicum.shareit.exceptions;

public class UserOrItemNotExist extends RuntimeException {
    public UserOrItemNotExist(String message) {
        super(message);
    }
}
