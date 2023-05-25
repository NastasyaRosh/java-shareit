package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.user.UserController;

import javax.validation.ValidationException;

@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class, BookingController.class,
        RequestController.class})
public class ErrorHandler {

    @ExceptionHandler({WrongStateException.class, ValidationException.class, MethodArgumentNotValidException.class})
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
