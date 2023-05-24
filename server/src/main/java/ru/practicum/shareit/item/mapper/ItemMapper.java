package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.InItemDto;
import ru.practicum.shareit.item.dto.OutItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static OutItemDto toItemDto(
            Item item,
            Long userId
    ) {
        OutItemDto itemDto = OutItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(item.getComments() != null ?
                        CommentMapper.mapToCommentDto(item.getComments()) : new ArrayList<>())
                .requestId(item.getRequest() != null ?
                        item.getRequest().getId() : null)
                .build();

        if (userId.equals(item.getOwner().getId())) {
            itemDto.setLastBooking(item.getLastBooking() != null ?
                    BookingMapper.toShortBookingDto(item.getLastBooking()) : null);
            itemDto.setNextBooking(item.getNextBooking() != null ?
                    BookingMapper.toShortBookingDto(item.getNextBooking()) : null);
        }
        return itemDto;
    }

    public static Item toItem(InItemDto inItemDto) {
        return Item.builder()
                .id(inItemDto.getId())
                .name(inItemDto.getName())
                .description(inItemDto.getDescription())
                .available(inItemDto.getAvailable())
                .build();
    }

    public static List<OutItemDto> listToItemDto(List<Item> items, Long userId) {
        return items.stream().map(item -> toItemDto(item, userId)).collect(Collectors.toList());
    }

    public static ShortItemDto toShortItemDto(Item item) {
        return ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}
