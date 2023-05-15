package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemTestUtil;
import ru.practicum.shareit.util.UserTestUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.util.ItemTestUtil.getItem;
import static ru.practicum.shareit.util.RequestTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.ANOTHER_USER_ID;
import static ru.practicum.shareit.util.UserTestUtil.USER_ID;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    ItemRequestDao requestRep;
    @Mock
    ItemDao itemRep;
    @Mock
    UserService userService;
    ItemRequestService requestService;
    final Pageable pageable = PageRequest.of(0 / 30, 30, Sort.by("created").descending());
    Item item;
    private static final LocalDateTime dt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    @BeforeEach
    void beforeEach() {
        requestService = new ItemRequestService(requestRep, userService, itemRep);
        item = getItem();
        item.setRequest(getRequest(dt));
    }

    @Test
    void shouldReturnRequestsWhenCallFindAll() {
        when(requestRep.findAllByRequesterIdIsNot(USER_ID, pageable)).thenReturn(getRequestsPage(dt));
        when(requestRep.findAllByRequesterIdIsNot(ANOTHER_USER_ID, pageable)).thenReturn(new PageImpl<>(emptyList()));
        when(itemRep.findAllByRequestIn(any())).thenReturn(ItemTestUtil.getListItemsWithRequest());

        assertEquals(getRequestsPage(dt).getContent(), requestService.getAllRequests(USER_ID, 0, 30));
        assertEquals(emptyList(), requestService.getAllRequests(ANOTHER_USER_ID, 0, 30));
    }

    @Test
    void shouldReturnRequestsWhenCallFindAllByUserId() {
        when(requestRep.findByRequesterId(USER_ID, requestRep.CREATED_DESC)).thenReturn(getRequestsList(dt));
        when(requestRep.findByRequesterId(ANOTHER_USER_ID, requestRep.CREATED_DESC)).thenReturn(emptyList());
        when(itemRep.findAllByRequestIn(any())).thenReturn(ItemTestUtil.getListItemsWithRequest());

        assertEquals(getRequestsPage(dt).getContent(), requestService.getAllMyItemRequests(USER_ID));

        assertEquals(emptyList(), requestService.getAllMyItemRequests(ANOTHER_USER_ID));
    }

    @Test
    void shouldReturnRequestWhenCallFindById() {
        when(requestRep.findById(REQUEST_ID)).thenReturn(Optional.of(getRequest(dt)));
        when(requestRep.findById(ANOTHER_REQUEST_ID)).thenReturn(Optional.empty());

        assertEquals(getRequest(dt), requestService.getItemRequestById(USER_ID, REQUEST_ID));
        assertThrows(EntityNotFoundException.class, () -> requestService.getItemRequestById(ANOTHER_USER_ID, ANOTHER_REQUEST_ID));
    }

    @Test
    void shouldCreateRequestWhenCallCreate() {
        //when(requestRep.existsById(REQUEST_ID)).thenReturn(false);
        //when(requestRep.existsById(ANOTHER_REQUEST_ID)).thenReturn(true);
        when(requestRep.save(any())).thenAnswer(returnsFirstArg());
        when(userService.findById(USER_ID)).thenReturn(UserTestUtil.getUser());

        ItemRequest itemRequest = requestService.createRequest(getRequestDto(dt), USER_ID);
        itemRequest.setId(1L);
        assertEquals(getRequest(dt), itemRequest);
        ItemRequest request = getRequest(dt);
        request.setId(ANOTHER_REQUEST_ID);
        //assertThrows(AlreadyExistsException.class, () -> requestService.createRequest(request, USER_ID));
    }
}
