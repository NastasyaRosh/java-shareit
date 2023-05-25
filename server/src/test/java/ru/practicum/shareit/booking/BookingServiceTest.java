package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.WrongDatesException;
import ru.practicum.shareit.exceptions.WrongStateException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemTestUtil;
import ru.practicum.shareit.util.UserTestUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.util.BookingTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    BookingDao bookingRep;
    @Mock
    UserService userService;
    BookingService bookingService;
    @Mock
    ItemService itemService;

    private final LocalDateTime dt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingService(bookingRep, userService, itemService);
    }

    @Test
    void shouldReturnBookingsWhenCallFindAllByBookerId() {
        when(userService.findById(USER_ID)).thenReturn(getUser());
        when(bookingRep.findByUserId(any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserIdAndStatus(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserCurrent(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserFuture(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserPast(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));

        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.ALL), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.WAITING), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.REJECTED), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.CURRENT), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.FUTURE), 0, 30).get(0));
        assertEquals(getBookingsList(dt),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.PAST), 0, 30));
        assertThrows(WrongStateException.class, () -> bookingService.findAllByBooker(USER_ID, "WRONG STATE", 0, 30));
    }

    @Test
    void shouldReturnBookingsWhenCallFilterUserBookingsByState() {
        when(userService.findById(USER_ID)).thenReturn(getUser());
        when(bookingRep.findByUserId(any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserIdAndStatus(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserCurrent(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserFuture(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserPast(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));

        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.ALL), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.WAITING), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.REJECTED), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.CURRENT), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.FUTURE), 0, 30).get(0));
        assertEquals(getBookingsList(dt),
                bookingService.findAllByBooker(USER_ID, String.valueOf(State.PAST), 0, 30));
    }

    @Test
    void shouldReturnBookingsWhenCallFindAllByItemOwnerId() {
        when(userService.findById(USER_ID)).thenReturn(getUser());
        when(bookingRep.findByUserId(any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserIdAndStatus(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserCurrent(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserFuture(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));
        when(bookingRep.findByUserPast(any(), any(), any(), any())).thenReturn(getBookingsPage(dt));

        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, String.valueOf(State.ALL), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, String.valueOf(State.WAITING), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, String.valueOf(State.REJECTED), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, String.valueOf(State.CURRENT), 0, 30).get(0));
        assertEquals(getBookingsList(dt).get(0),
                bookingService.findAllByItemsOwnerId(USER_ID, String.valueOf(State.FUTURE), 0, 30).get(0));
        assertEquals(getBookingsList(dt),
                bookingService.findAllByItemsOwnerId(USER_ID, String.valueOf(State.PAST), 0, 30));
    }

    @Test
    void shouldReturnBookingOrThrowWhenCallFindById() {
        when(bookingRep.findById(BOOKING_ID)).thenReturn(Optional.of(getBooking(dt)));
        when(bookingRep.findById(ANOTHER_BOOKING_ID)).thenReturn(Optional.empty());

        assertEquals(getBooking(dt), bookingService.getBookingById(BOOKING_ID, USER_ID));
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingById(ANOTHER_BOOKING_ID, USER_ID));
        assertThrows(AccessException.class, () -> bookingService.getBookingById(BOOKING_ID, ANOTHER_USER_ID));
    }

    @Test
    void shouldCreateBookingWhenCallCreateBooking() {
        when(bookingRep.save(any())).thenAnswer(returnsFirstArg());
        when(itemService.getItem(USER_ID)).thenReturn(ItemTestUtil.getItem());
        when(userService.findById(any())).thenReturn(UserTestUtil.getUser());

        assertThrows(AccessException.class, () -> bookingService.createBooking(getInputBookingDto(dt), USER_ID));

        Booking booking = getBooking(dt);
        booking.getItem().setAvailable(false);

        assertThrows(WrongDatesException.class, () -> bookingService.createBooking(getInputBookingDtoWrongDate(dt), ANOTHER_USER_ID));
        Booking bookingOut = bookingService.createBooking(getInputBookingDto(dt), ANOTHER_USER_ID);
        bookingOut.setId(1);
        assertEquals(getBooking(dt), bookingOut);
    }
}
