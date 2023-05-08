package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.WrongDatesException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingDao bookingRepository;
    private final UserService userService;
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

    public List<Booking> findAllByBooker(Long bookerId, State state) {
        userService.findById(bookerId);
        return userBookingsByState(bookerId, state, false);
    }

    public List<Booking> findAllByItemsOwnerId(Long ownerId, State state) {
        userService.findById(ownerId);
        return userBookingsByState(ownerId, state, true);
    }

    private void checkBookingCreate(Booking booking, Long userId) {
        Item item = booking.getItem();
        if (item.getOwner().getId() == userId) {
            throw new AccessException("Пользователь не может бронировать свою же вещь.");
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
            throw new AccessException("Пользователь не может распоряжаться чужой вещью.");
        }
        if (!BookingStatuses.WAITING.equals(booking.getStatus())) {
            throw new ValidationException("Решение уже было принято.");
        }
    }

    private List<Booking> userBookingsByState(Long userId, State state, Boolean isOwner) {
        List<Booking> outList = null;
        switch (state) {
            case ALL:
                outList = bookingRepository.findByUserId(userId, isOwner, bookingRepository.SORT_DESC);
                break;
            case WAITING:
                outList = bookingRepository.findByUserIdAndStatus(userId, isOwner, BookingStatuses.WAITING
                        , bookingRepository.SORT_DESC);
                break;
            case REJECTED:
                outList = bookingRepository.findByUserIdAndStatus(userId, isOwner, BookingStatuses.REJECTED
                        , bookingRepository.SORT_DESC);
                break;
            case CURRENT:
                outList = bookingRepository.findByUserCurrent(userId, isOwner, LocalDateTime.now()
                        , bookingRepository.SORT_DESC);
                break;
            case PAST:
                outList = bookingRepository.findByUserPast(userId, isOwner, LocalDateTime.now()
                        , bookingRepository.SORT_DESC);
                break;
            case FUTURE:
                outList = bookingRepository.findByUserFuture(userId, isOwner, LocalDateTime.now()
                        , bookingRepository.SORT_DESC);
                break;
        }
        return outList;
    }

}
