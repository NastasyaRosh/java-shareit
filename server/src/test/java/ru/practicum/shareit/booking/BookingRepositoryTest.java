package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.model.BookingStatuses.*;
import static ru.practicum.shareit.util.BookingTestUtil.getNewBooking;
import static ru.practicum.shareit.util.ItemTestUtil.getNewItem;
import static ru.practicum.shareit.util.UserTestUtil.getNewAnotherUser;
import static ru.practicum.shareit.util.UserTestUtil.getNewUser;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final EntityManager em;
    @Autowired
    private BookingDao bookingRep;

    final Pageable pageable = PageRequest.of(0 / 30, 30, Sort.by("start").descending());
    private static LocalDateTime dt;

    @BeforeAll
    static void beforeAll() {
        dt = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }

    @Test
    void callFindByUserIdApproved() {
        User owner = getNewUser();
        User booker = getNewAnotherUser();
        Item item = getNewItem(owner);
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        Booking booking = getNewBooking(item, booker, APPROVED, dt);
        em.persist(booking);

        List<Booking> returnedByOwner = bookingRep
                .findByUserId(owner.getId(), true, pageable).getContent();
        assertEquals(1, returnedByOwner.size());
        assertEquals(booking, returnedByOwner.get(0));
        assertEquals(APPROVED, returnedByOwner.get(0).getStatus());

        List<Booking> returnedByBooker = bookingRep
                .findByUserId(booker.getId(), false, pageable).getContent();
        assertEquals(1, returnedByBooker.size());
        assertEquals(booking, returnedByBooker.get(0));
    }

    @Test
    void callFindByUserIdAndStatusWaiting() {
        User owner = getNewUser();
        User booker = getNewAnotherUser();
        Item item = getNewItem(owner);
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        Booking booking = getNewBooking(item, booker, WAITING, dt);
        em.persist(booking);

        List<Booking> returnedByOwner = bookingRep
                .findByUserIdAndStatus(owner.getId(), true, WAITING, pageable).getContent();
        assertEquals(1, returnedByOwner.size());
        assertEquals(booking, returnedByOwner.get(0));
        assertEquals(WAITING, returnedByOwner.get(0).getStatus());

        List<Booking> returnedByBooker = bookingRep
                .findByUserIdAndStatus(booker.getId(), false, WAITING, pageable).getContent();
        assertEquals(1, returnedByBooker.size());
        assertEquals(booking, returnedByBooker.get(0));
    }

    @Test
    void callFindByUserIdAndStatusRejected() {
        User owner = getNewUser();
        User booker = getNewAnotherUser();
        Item item = getNewItem(owner);
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        Booking booking = getNewBooking(item, booker, REJECTED, dt);
        em.persist(booking);

        List<Booking> returnedByOwner = bookingRep
                .findByUserIdAndStatus(owner.getId(), true, REJECTED, pageable).getContent();
        assertEquals(1, returnedByOwner.size());
        assertEquals(booking, returnedByOwner.get(0));
        assertEquals(REJECTED, returnedByOwner.get(0).getStatus());

        List<Booking> returnedByBooker = bookingRep
                .findByUserIdAndStatus(booker.getId(), false, REJECTED, pageable).getContent();
        assertEquals(1, returnedByBooker.size());
        assertEquals(booking, returnedByBooker.get(0));
    }

    @Test
    void callFindByUserCurrent() {
        User owner = getNewUser();
        User booker = getNewAnotherUser();
        Item item = getNewItem(owner);
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        Booking booking = getNewBooking(item, booker, APPROVED, dt.minusDays(1));
        em.persist(booking);

        List<Booking> returnedByOwner = bookingRep
                .findByUserCurrent(owner.getId(), true, dt, pageable).getContent();
        assertEquals(1, returnedByOwner.size());
        assertEquals(booking, returnedByOwner.get(0));
        assertTrue(dt.isAfter(returnedByOwner.get(0).getStart()));
        assertTrue(dt.isBefore(returnedByOwner.get(0).getEnd()));

        List<Booking> returnedByBooker = bookingRep
                .findByUserCurrent(booker.getId(), false, dt, pageable).getContent();
        assertEquals(1, returnedByBooker.size());
        assertEquals(booking, returnedByBooker.get(0));
    }

    @Test
    void callFindByUserPast() {
        User owner = getNewUser();
        User booker = getNewAnotherUser();
        Item item = getNewItem(owner);
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        Booking booking = getNewBooking(item, booker, APPROVED, dt.minusDays(3));
        em.persist(booking);

        List<Booking> returnedByOwner = bookingRep
                .findByUserPast(owner.getId(), true, dt, pageable).getContent();
        assertEquals(1, returnedByOwner.size());
        assertEquals(booking, returnedByOwner.get(0));
        assertTrue(dt.isAfter(returnedByOwner.get(0).getStart()));
        assertTrue(dt.isAfter(returnedByOwner.get(0).getEnd()));

        List<Booking> returnedByBooker = bookingRep
                .findByUserPast(booker.getId(), false, dt, pageable).getContent();
        assertEquals(1, returnedByBooker.size());
        assertEquals(booking, returnedByBooker.get(0));
    }

    @Test
    void callFindByUserFuture() {
        User owner = getNewUser();
        User booker = getNewAnotherUser();
        Item item = getNewItem(owner);
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        Booking booking = getNewBooking(item, booker, APPROVED, dt.plusDays(1));
        em.persist(booking);

        List<Booking> returnedByOwner = bookingRep
                .findByUserFuture(owner.getId(), true, dt, pageable).getContent();
        assertEquals(1, returnedByOwner.size());
        assertEquals(booking, returnedByOwner.get(0));
        assertTrue(dt.isBefore(returnedByOwner.get(0).getStart()));
        assertTrue(dt.isBefore(returnedByOwner.get(0).getEnd()));

        List<Booking> returnedByBooker = bookingRep
                .findByUserFuture(booker.getId(), false, dt, pageable).getContent();
        assertEquals(1, returnedByBooker.size());
        assertEquals(booking, returnedByBooker.get(0));
    }
}
