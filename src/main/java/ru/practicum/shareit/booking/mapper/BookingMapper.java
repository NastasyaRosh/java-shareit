package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.exceptions.WrongDatesException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static OutBookingDto toBookingDto(Booking booking){
        return OutBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(InBookingDto inBookingDto, Item item, User user){
        if ((inBookingDto.getStart() == null) || (inBookingDto.getEnd() == null)) {
            throw new WrongDatesException("Проверьте запрашиваемые даты.");
        }
        return Booking.builder()
                .id(inBookingDto.getId())
                .start(inBookingDto.getStart())
                .end(inBookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatuses.WAITING)
                .build();
    }

    public static List<OutBookingDto> mapToBookingDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
