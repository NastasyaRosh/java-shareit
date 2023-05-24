package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

//import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingDao bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    public Booking createBooking(InBookingDto inBookingDto, Long userId) {
        User booker = userService.findById(userId);
        Item item = itemService.getItem(inBookingDto.getItemId());
        Booking booking = BookingMapper.toBooking(inBookingDto, item, booker);
        booking.setStatus(BookingStatuses.WAITING);
        checkBookingCreate(booking, userId);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Запрашиваемого бронирования не существует.")
        );
        checkBookingUpdate(booking, userId);
        booking.setStatus(approved ? BookingStatuses.APPROVED : BookingStatuses.REJECTED);
        return booking;
    }

    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Запрашиваемого бронирования не существует.")
        );
        if ((booking.getItem().getOwner().getId() != userId)
                && (booking.getBooker().getId() != userId)) {
            throw new AccessException("Выбранный пользователь не может видеть запрашиваемые данные.");
        }
        return booking;
    }

    public List<Booking> findAllByBooker(Long bookerId, String state, Integer from, Integer size) {
        State bookingState = State.checkState(state);
        userService.findById(bookerId);
        return userBookingsByState(bookerId, bookingState, false, from, size);
    }

    public List<Booking> findAllByItemsOwnerId(Long ownerId, String state, Integer from, Integer size) {
        State bookingState = State.checkState(state);
        userService.findById(ownerId);
        return userBookingsByState(ownerId, bookingState, true, from, size);
    }

    private void checkBookingCreate(Booking booking, Long userId) {
        Item item = booking.getItem();
        if (item.getOwner().getId() == userId) {
            throw new AccessException("Пользователь не может бронировать свою же вещь.");
        }
        if (!item.getAvailable()) {
            throw new AvailableException("Вещь не доступна.");
        }
        if ((booking.getEnd().isBefore(booking.getStart())) || (booking.getEnd().equals(booking.getStart()))
                || (booking.getStart().isBefore(LocalDateTime.now()))) {
            throw new WrongDatesException("Проверьте запрашиваемые даты.");
        }
    }

    private void checkBookingUpdate(Booking booking, Long userId) {
        if (booking.getItem().getOwner().getId() != userId) {
            throw new AccessException("Пользователь не может распоряжаться чужой вещью.");
        }
        if (!BookingStatuses.WAITING.equals(booking.getStatus())) {
            throw new AvailableException("Решение уже было принято.");
        }
    }

    private List<Booking> userBookingsByState(Long userId, State state, Boolean isOwner, Integer from, Integer size) {
        List<Booking> outList = null;
        //checkPageableParams(from, size);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        switch (state) {
            case ALL:
                outList = bookingRepository.findByUserId(userId, isOwner, pageable).getContent();
                break;
            case WAITING:
                outList = bookingRepository.findByUserIdAndStatus(userId, isOwner, BookingStatuses.WAITING,
                        pageable).getContent();
                break;
            case REJECTED:
                outList = bookingRepository.findByUserIdAndStatus(userId, isOwner, BookingStatuses.REJECTED,
                        pageable).getContent();
                break;
            case CURRENT:
                outList = bookingRepository.findByUserCurrent(userId, isOwner, LocalDateTime.now(),
                        pageable).getContent();
                break;
            case PAST:
                outList = bookingRepository.findByUserPast(userId, isOwner, LocalDateTime.now(),
                        pageable).getContent();
                break;
            case FUTURE:
                outList = bookingRepository.findByUserFuture(userId, isOwner, LocalDateTime.now(),
                        pageable).getContent();
                break;
            default:
                throw new WrongStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return outList;
    }

/*    private void checkPageableParams(Integer from, Integer size) {
        if ((from < 0) || (size <= 0)) {
            throw new ValidationException("Введите верные данные для пагинации.");
        }
    }*/

}
