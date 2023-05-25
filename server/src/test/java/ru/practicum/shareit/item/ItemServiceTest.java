package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemTestUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.util.BookingTestUtil.getBookingsList;
import static ru.practicum.shareit.util.CommentTestUtil.getComment;
import static ru.practicum.shareit.util.ItemTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemDao itemRep;
    @Mock
    CommentDao commentRep;
    @Mock
    BookingDao bookingRep;
    @Mock
    UserService userService;

    ItemService itemService;
    ItemRequestService itemRequestService;

    private final User user = getUser();
    private final Item item = getItem();
    private final Item updatedItem = getUpdatedItem();
    private final Comment comment = getComment();

    @BeforeEach
    void beforeEach() {
        itemService = new ItemService(itemRep, bookingRep, commentRep, userService, itemRequestService);
    }

    @Test
    void shouldReturnItemListWhenCallFindByUserId() {
        when(itemRep.findAllByOwnerId(USER_ID)).thenReturn(List.of(item));
        when(bookingRep.findByItemInAndStatus(any(), any(), any()))
                .thenReturn(getBookingsList(LocalDateTime.now()));

        List<Item> items = itemService.getAllMyItems(USER_ID);

        verify(itemRep, times(1)).findAllByOwnerId(USER_ID);
        assertThat(items, notNullValue());
        assertEquals(1, items.size());
        assertEquals(ITEM_ID, items.get(0).getId());
        assertEquals(item.getOwner(), items.get(0).getOwner());
    }

    @Test
    void shouldReturnItemOrThrowWhenCallFindByItemId() {
        when(itemRep.findById(ITEM_ID)).thenReturn(Optional.ofNullable(item));
        when(itemRep.findById(ANOTHER_ITEM_ID)).thenReturn(Optional.empty());

        assertEquals(item, itemService.getItem(ITEM_ID));
        verify(itemRep, times(1)).findById(ITEM_ID);
        verifyNoMoreInteractions(itemRep);

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(ANOTHER_ITEM_ID));
    }

    @Test
    void shouldReturnItemsListWhenCallSearchByText() {
        when(itemRep.searchItems("descr")).thenReturn(List.of(item));
        when(itemRep.searchItems("undefined")).thenReturn(emptyList());

        List<Item> items = itemService.searchItems("descr");
        assertThat(items, notNullValue());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getOwner(), items.get(0).getOwner());

        List<Item> empty = itemService.searchItems("undefined");
        assertThat(empty, notNullValue());
        assertEquals(0, empty.size());
    }

    @Test
    void shouldCreateAndReturnItemWhenCallCreate() {
        when(itemRep.save(any())).thenAnswer(returnsFirstArg());
        when(userService.findById(USER_ID)).thenReturn(user);

        Item returned = itemService.createItem(ItemTestUtil.getInputItemDto(), USER_ID);
        verify(userService, times(1)).findById(USER_ID);
        verify(itemRep, times(1)).save(any());
        assertEquals(item, returned);
    }

    @Test
    void shouldUpdateAndReturnItemWhenCallUpdate() {
        when(itemRep.findById(ITEM_ID)).thenReturn(Optional.ofNullable(item));
        when(itemRep.findById(ANOTHER_ITEM_ID)).thenReturn(Optional.empty());
        when(itemRep.findAllByOwnerId(USER_ID)).thenReturn(List.of(item));

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(updatedItem, ANOTHER_ITEM_ID, USER_ID));

        Item returned = itemService.updateItem(updatedItem, ITEM_ID, USER_ID);
        updatedItem.setComments(new ArrayList<>());
        assertEquals(updatedItem, returned);
        verify(itemRep, times(3)).findById(anyLong());
    }

    @Test
    void shouldCreateCommentWhenCallCreateComment() {
        when(bookingRep.findAllRealItemBookingsForUserAtTheMoment(anyLong(), anyLong(), any()))
                .thenReturn(getBookingsList(LocalDateTime.now()));
        when(commentRep.save(any())).thenReturn(comment);
        when(itemRep.findById(ITEM_ID)).thenReturn(Optional.ofNullable(item));

        Comment returned = itemService.createComment(USER_ID, ITEM_ID, comment.getText());

        assertEquals(comment, returned);
        verify(bookingRep, times(1))
                .findAllRealItemBookingsForUserAtTheMoment(anyLong(), anyLong(), any());
        verify(commentRep, times(1)).save(any());
    }
}
