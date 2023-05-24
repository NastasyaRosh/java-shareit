package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.util.BookingTestUtil.*;
import static ru.practicum.shareit.util.ItemTestUtil.ITEM_ID;
import static ru.practicum.shareit.util.ItemTestUtil.getItem;
import static ru.practicum.shareit.util.TestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.USER_ID;
import static ru.practicum.shareit.util.UserTestUtil.getUser;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;
    @MockBean
    ItemService itemService;
    @MockBean
    BookingService bookingService;

    LocalDateTime dt;

    @BeforeEach
    void beforeEach() {
        dt = LocalDateTime.now();
    }


    @Test
    void findById() throws Exception {
        when(bookingService.getBookingById(BOOKING_ID, USER_ID)).thenReturn(getBooking(dt));

        mvc.perform(getGetReq(BOOKING_PATH, USER_ID))
                .andExpect(OK)
                .andExpect(jsonPath("$.id", is(getBooking(dt).getId()), Long.class));
    }

    @Test
    void findAllByBooker() throws Exception {
        when(bookingService.findAllByBooker(USER_ID, String.valueOf(State.WAITING), 0, 30))
                .thenReturn(getBookingsList(dt));

        mvc.perform(getGetReq(BOOKING_DEFAULT_PATH + "?state=WAITING", USER_ID))
                .andExpect(OK)
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllByItemsOwner() throws Exception {
        when(bookingService.findAllByItemsOwnerId(any(), any(), any(), any()))
                .thenReturn(emptyList());

        mvc.perform(getGetReq(BOOKING_DEFAULT_PATH + "/owner", USER_ID))
                .andExpect(OK)
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void create() throws Exception {
        when(userService.findById(USER_ID)).thenReturn(getUser());
        when(itemService.getItem(ITEM_ID)).thenReturn(getItem());
        when(bookingService.createBooking(any(), any())).thenReturn(getBooking(dt));

        mvc.perform(getPostReq(BOOKING_DEFAULT_PATH, USER_ID)
                        .content(mapper.writeValueAsString(getInputBookingDto(dt))))
                .andExpect(OK)
                .andExpect(content().json(mapper.writeValueAsString(getOutputBookingDto(dt))));
    }

    @Test
    void approve() throws Exception {
        when(bookingService.updateBooking(USER_ID, BOOKING_ID, true)).thenReturn(getBooking(dt));

        mvc.perform(getPatchReq(BOOKING_PATH + "?approved=true", USER_ID))
                .andExpect(OK)
                .andExpect(content().json(mapper.writeValueAsString(getOutputBookingDto(dt))));
    }
}