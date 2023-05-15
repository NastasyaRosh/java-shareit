package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.util.ItemTestUtil.ITEM_ID;

@UtilityClass
public class BookingTestUtil {
    public static final String BOOKING_DEFAULT_PATH = "/bookings";
    public static final Long BOOKING_ID = 1L;
    public static final Long ANOTHER_BOOKING_ID = 2L;
    public static final BookingStatuses DEFAULT_STATUS = BookingStatuses.WAITING;
    public static final String BOOKING_PATH = BOOKING_DEFAULT_PATH + "/" + BOOKING_ID;

    public static Booking getBooking(LocalDateTime dt) {
        return new Booking(
                BOOKING_ID,
                dt,
                dt.plusDays(1).truncatedTo(ChronoUnit.MINUTES),
                ItemTestUtil.getItem(),
                UserTestUtil.getUser(),
                DEFAULT_STATUS
        );
    }

    public static InBookingDto getInputBookingDto(LocalDateTime dt) {
        return InBookingDto.builder()
                .start(dt.plusDays(1).truncatedTo(ChronoUnit.MINUTES))
                .end(dt.plusDays(2).truncatedTo(ChronoUnit.MINUTES))
                .itemId(ITEM_ID)
                .build();
    }

    public static OutBookingDto getOutputBookingDto(LocalDateTime dt) {
        return BookingMapper.toBookingDto(getBooking(dt));
    }

    public static Booking getBookingWithStatus(LocalDateTime dt, BookingStatuses status) {
        return new Booking(
                BOOKING_ID,
                dt.truncatedTo(ChronoUnit.MINUTES),
                dt.plusDays(1).truncatedTo(ChronoUnit.MINUTES),
                ItemTestUtil.getItem(),
                UserTestUtil.getUser(),
                status
        );
    }

    public static List<Booking> getBookingsList(LocalDateTime dt) {
        Booking prevBooking = getBooking(dt);
        prevBooking.setStart(dt.truncatedTo(ChronoUnit.MINUTES));
        prevBooking.setEnd(dt.minusDays(1).truncatedTo(ChronoUnit.MINUTES));

        Booking nextBooking = getBooking(dt);
        nextBooking.setId(ANOTHER_BOOKING_ID);
        nextBooking.setStart(dt.plusDays(1).truncatedTo(ChronoUnit.MINUTES));
        nextBooking.setEnd(dt.plusDays(2).truncatedTo(ChronoUnit.MINUTES));

        List<Booking> bookings = new ArrayList<>();
        bookings.add(prevBooking);
        bookings.add(nextBooking);

        return bookings;
    }

    public static Booking getNewBooking(Item item, User booker, BookingStatuses status, LocalDateTime dt) {
        return Booking.builder()
                .start(dt)
                .end(dt.plusDays(2))
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }
}

