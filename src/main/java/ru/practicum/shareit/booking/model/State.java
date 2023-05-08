package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exceptions.WrongStateException;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State checkState(String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new WrongStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
    }
