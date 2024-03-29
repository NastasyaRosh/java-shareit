package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class OutBookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ShortItemDto item;
    private UserDto booker;
    private BookingStatuses status;
}
