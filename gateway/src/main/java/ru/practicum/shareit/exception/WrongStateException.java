package ru.practicum.shareit.exception;

public class WrongStateException extends IllegalArgumentException {

    public WrongStateException(String state) {
        super(String.format("Wrong state: %s", state));
    }
}
