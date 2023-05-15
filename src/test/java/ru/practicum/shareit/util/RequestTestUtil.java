package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.util.UserTestUtil.ANOTHER_USER_ID;
import static ru.practicum.shareit.util.UserTestUtil.USER_ID;

@UtilityClass
public class RequestTestUtil {
    public static final String REQUEST_DEFAULT_PATH = "/requests";
    public static final Long REQUEST_ID = 1L;
    public static final String REQUEST_DESCR = "Description";
    public static final Long ANOTHER_REQUEST_ID = 2L;
    public static final String REQUEST_PATH = REQUEST_DEFAULT_PATH + "/" + REQUEST_ID;

    public static ItemRequest getRequest(LocalDateTime dt) {
        return new ItemRequest(REQUEST_ID, REQUEST_DESCR, UserTestUtil.getUser(), dt, new ArrayList<>());
    }

    public static List<ItemRequest> getRequestsList(LocalDateTime dt) {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(new ItemRequest(REQUEST_ID, REQUEST_DESCR, UserTestUtil.getUser(), dt, new ArrayList<>()));
        requests.add(new ItemRequest(ANOTHER_REQUEST_ID, REQUEST_DESCR, UserTestUtil.getUser(), dt, new ArrayList<>()));
        requests.get(1).getRequester().setId(ANOTHER_USER_ID);
        return requests;
    }

    public static ItemRequestDto getRequestDto(LocalDateTime dt) {
        return new ItemRequestDto(null, REQUEST_DESCR, UserTestUtil.getUserDto(), dt, List.of(ItemTestUtil.getOutDto(USER_ID)));
    }

    public static ItemRequestDto getOutputRequestDto(LocalDateTime dt) {
        return ItemRequestMapper.toItemRequestDto(getRequest(dt));
    }

    public static Page<ItemRequest> getRequestsPage(LocalDateTime dt) {
        return new PageImpl<>(getRequestsList(dt));
    }
}
