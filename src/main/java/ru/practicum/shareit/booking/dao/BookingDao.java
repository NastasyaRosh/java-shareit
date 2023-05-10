package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingDao extends JpaRepository<Booking, Long>{
    Sort START_DESC = Sort.by(Sort.Direction.DESC, "start");
    Sort START_ASC = Sort.by(Sort.Direction.ASC, "start");

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 ")
    List<Booking> findByUserId(Long userId, Boolean isOwner, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "  and b.status = ?3 ")
    List<Booking> findByUserIdAndStatus(Long userId, Boolean isOwner, BookingStatuses status, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "  and b.start <= ?3 " +
            "  and b.end >= ?3 ")
    List<Booking> findByUserCurrent(Long userId, Boolean isOwner, LocalDateTime now, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "  and b.start >= ?3 ")
    List<Booking> findByUserFuture(Long userId, Boolean isOwner, LocalDateTime now, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where (case when ?2 = true then b.item.owner.id else b.booker.id end) = ?1 " +
            "  and b.end <= ?3 ")
    List<Booking> findByUserPast(Long userId, Boolean isOwner, LocalDateTime now, Sort sort);

    Booking findTopByItemIdAndStatusAndStartLessThanEqual(Long itemId, BookingStatuses status, LocalDateTime now, Sort sort);

    Booking findTopByItemIdAndStatusAndStartAfter(Long itemId,BookingStatuses status, LocalDateTime now, Sort sort);

    List<Booking> findByItemInAndStatus(List<Item> items, BookingStatuses status, Sort sort);

    /*@Query("select b from Booking  b where b.item.id = ?1 and b.booker.id = ?2" +
            " and ((b.status = 'APPROVED' and b.start < ?3) or (b.status = 'CANCELED'))")*/
    @Query("select b from Booking  b where b.item.id = ?1 and b.booker.id = ?2" +
            " and (b.status = 'APPROVED' and b.end < ?3)")
    List<Booking> findAllRealItemBookingsForUserAtTheMoment(Long itemId, Long userId, LocalDateTime currentTime);
}
