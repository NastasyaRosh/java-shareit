package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.AvailableException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.InItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

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
    private final BookingDao bookingRepository;
    private final CommentDao commentRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Transactional
    public Item createItem(InItemDto inItemDto, Long userId) {
        Item item = ItemMapper.toItem(inItemDto);
        checkCreationRequest(inItemDto, item, userId);
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
        setBookingsForItem(item);
        item.setComments(commentRepository.findByItemId(itemId, commentRepository.CREATED_DESC));
        return item;
    }

    public List<Item> getAllMyItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId, Sort.by("id"));
        return setBookingsForListItems(items);
    }

    public List<Item> searchItems(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text);
    }

    @Transactional
    public Comment createComment(Long userId, Long itemId, String text) {
        checkComment(itemId, userId, text);
        User user = userService.findById(userId);
        Item item = getItem(itemId);
        Comment comment = Comment.builder().text(text).author(user).item(item).created(LocalDateTime.now()).build();
        return commentRepository.save(comment);
    }

    private void checkCreationRequest(InItemDto inItemDto, Item item, Long userId) {
        if (inItemDto.getRequestId() != null) {
            item.setRequest(itemRequestService.getItemRequestById(userId, inItemDto.getRequestId()));
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

    private void setBookingsForItem(Item item) {
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
    }

    private List<Item> setBookingsForListItems(List<Item> items) {
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemInAndStatus(
                        items, BookingStatuses.APPROVED, bookingRepository.START_DESC).stream()
                .collect(groupingBy(Booking::getItem, toList()));
        for (Item item : items) {
            Booking lastBooking = null;
            Booking nextBooking = null;
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

    private void checkComment(Long itemId, Long userId, String text) {
        List<Booking> bookings =
                bookingRepository.findAllRealItemBookingsForUserAtTheMoment(itemId, userId, LocalDateTime.now());
        if (bookings.size() == 0) {
            throw new AvailableException(String.format("Пользователь с id = %s не может комментировать вещь с id = %s.",
                    userId, itemId));
        }
    }
}