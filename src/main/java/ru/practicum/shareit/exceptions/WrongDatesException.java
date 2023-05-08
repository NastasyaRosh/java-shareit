package ru.practicum.shareit.exceptions;

public class WrongDatesException extends RuntimeException {
    public WrongDatesException(String message) {
        super(message);
    }
}
