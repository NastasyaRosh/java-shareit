package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    @PostMapping
    public OutBookingDto createBooking(@RequestHeader("x-sharer-user-id") Long userId, @RequestBody InBookingDto inBookingDto) {
        log.debug("Создание запроса на бронирование.");
        User booker = userService.findById(userId);
        Item item = itemService.getItem(inBookingDto.getItemId());
        Booking outBooking = BookingMapper.toBooking(inBookingDto, item, booker);
        return BookingMapper.toBookingDto(bookingService.createBooking(outBooking, userId));
    }

    @PatchMapping("/{bookingId}")
    public OutBookingDto updateBooking(@RequestHeader("x-sharer-user-id") Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        log.debug("Проверка запроса бронирования владельцем вещи.");
        return BookingMapper.toBookingDto(bookingService.updateBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public OutBookingDto getBookingById(@RequestHeader("x-sharer-user-id") Long userId,
                                             @PathVariable Long bookingId) {
        log.debug("Поиск бронирования c идентификатором: " + bookingId);
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

}
