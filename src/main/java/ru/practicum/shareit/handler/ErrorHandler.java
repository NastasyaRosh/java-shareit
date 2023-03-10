package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

import javax.validation.ValidationException;

@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class})
public class ErrorHandler {
    @ExceptionHandler({EntityNotFoundException.class, AccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notExist(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse alreadyExist(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationWrong(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherExceptions(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
