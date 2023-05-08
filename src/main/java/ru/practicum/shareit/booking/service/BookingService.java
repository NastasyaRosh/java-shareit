package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.WrongDatesException;
import ru.practicum.shareit.item.model.Item;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingDao bookingRepository;
    @Transactional
    public Booking createBooking (Booking booking, Long userId){
        booking.setStatus(BookingStatuses.WAITING);
        checkBookingCreate(booking, userId);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking updateBooking (Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Запрашиваемого бронирования не существует.")
        );
        checkBookingUpdate(booking, userId);
        booking.setStatus(approved ? BookingStatuses.APPROVED : BookingStatuses.REJECTED);
        return booking;
    }

    public Booking getBookingById (Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Запрашиваемого бронирования не существует.")
        );
        if ((booking.getItem().getOwner().getId() != userId)
                && (booking.getBooker().getId() != userId)) {
            throw new AccessException("Выбранный пользователь не может видеть запрашиваемые данные.");
        }
        return booking;
    }

    private void checkBookingCreate(Booking booking, Long userId) {
        Item item = booking.getItem();
        if (item.getOwner().getId() == userId) {
            throw new ValidationException("Пользователь не может бронировать свою же вещь.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна.");
        }
        if ((booking.getEnd().isBefore(booking.getStart())) || (booking.getEnd().equals(booking.getStart()))
            || (booking.getStart().isBefore(LocalDateTime.now()))) {
            throw new WrongDatesException("Проверьте запрашиваемые даты.");
        }
    }

    private void checkBookingUpdate(Booking booking, Long userId){
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ValidationException("Пользователь не может распоряжаться чужой вещью.");
        }
        if (!BookingStatuses.WAITING.equals(booking.getStatus())) {
            throw new ValidationException("Решение уже было принято.");
        }
    }

}
