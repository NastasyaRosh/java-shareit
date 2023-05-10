package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemDao itemRepository;
    private final UserService userService;
    private final BookingDao bookingRepository;

    @Transactional
    public Item createItem(Item item, Long userId) {
        checkCreationRequest(item);
        Item itemWithOwner = installOwner(item, userId);
        return itemRepository.save(itemWithOwner);
    }
    @Transactional
    public Item updateItem(Item item, Long itemId, Long userId) {
        validationUpdatingItem(itemId, userId);
        Item oldItem = getItem(itemId);
        setFieldsInUpdatingItem(item, itemId, oldItem);
        return oldItem;
    }

    public Item getItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Запрашиваемой вещи не существует."));
        return setBookingsForItem(item);
    }

    public List<Item> getAllMyItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return setBookingsForListItems(items);
    }

    public List<Item> searchItems(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text);
    }

    private void checkCreationRequest(Item item) {
        if (item.getName() == null || item.getDescription() == null
                || item.getName().isBlank() || item.getDescription().isBlank()) {
            throw new ValidationException("Передано пустое имя или описание.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Не передано значение доступности.");
        }
    }

    private Item installOwner(Item item, Long userId) {
        item.setOwner(userService.findById(userId));
        return item;
    }

    private void setFieldsInUpdatingItem(Item item, Long itemId, Item oldItem) {
        if (item.getName() == null) {
            item.setName(getItem(itemId).getName());
        } else {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(getItem(itemId).getDescription());
        } else {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(getItem(itemId).getAvailable());
        } else {
            oldItem.setAvailable(item.getAvailable());
        }
    }

    private void validationUpdatingItem(Long itemId, Long userId) {
        if (!(getAllMyItems(userId).contains(getItem(itemId)))) {
            throw new AccessException(String.format("Пользователь с id = %s не имеет доступа к вещи с id = %s", userId, itemId));
        }
    }

    private Item setBookingsForItem(Item item) {
        item.setLastBooking(
                bookingRepository.findTopByItemIdAndStatusAndStartLessThanEqual(
                        item.getId(),
                        BookingStatuses.APPROVED,
                        LocalDateTime.now(),
                        bookingRepository.START_DESC)
        );
        item.setNextBooking(
                bookingRepository.findTopByItemIdAndStatusAndStartAfter(
                        item.getId(),
                        BookingStatuses.APPROVED,
                        LocalDateTime.now(),
                        bookingRepository.START_ASC)
        );
        return item;
    }

    private List<Item> setBookingsForListItems(List<Item> items) {
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemInAndStatus(
                        items, BookingStatuses.APPROVED, bookingRepository.START_DESC).stream()
                .collect(groupingBy(Booking::getItem, toList()));
        /*Map<Item, List<Comment>> comments = commentRep.findByItemIn(items, commentRep.CREATED_DESC).stream()
                .collect(groupingBy(Comment::getItem, toList()));*/
        for (Item item : items) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            //item.setComments(comments.get(item));
            if (bookings.get(item) != null) {
                lastBooking = bookings.get(item).stream()
                        .filter(booking -> !booking.getStart().isAfter(LocalDateTime.now()))
                        .findFirst().orElse(null);
                nextBooking = bookings.get(item).stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .skip(1).findFirst().orElse(null);
            }
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
        }
        return items;
    }
}