package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.util.RequestTestUtil.*;
import static ru.practicum.shareit.util.TestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.USER_ID;
import static ru.practicum.shareit.util.UserTestUtil.getAnotherUser;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class RequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;
    @MockBean
    ItemRequestService requestService;

    LocalDateTime dt;

    @BeforeEach
    void beforeEach() {
        dt = LocalDateTime.now();
    }

    @Test
    void findAllByUserId() throws Exception {
        when(requestService.getAllMyItemRequests(USER_ID)).thenReturn(getRequestsList(dt));

        mvc.perform(getGetReq(REQUEST_DEFAULT_PATH, USER_ID))
                .andExpect(OK)
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void findById() throws Exception {
        when(requestService.getItemRequestById(USER_ID, REQUEST_ID)).thenReturn(getRequest(dt));

        mvc.perform(getGetReq(REQUEST_PATH, USER_ID))
                .andExpect(OK)
                .andExpect(content().json(mapper.writeValueAsString(getOutputRequestDto(dt))));
    }

    @Test
    void findAll() throws Exception {
        when(requestService.getAllRequests(USER_ID, 0, 30)).thenReturn(getRequestsList(dt));

        mvc.perform(getGetReq(REQUEST_DEFAULT_PATH + "/all", USER_ID))
                .andExpect(OK)
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void create() throws Exception {
        when(requestService.createRequest(any(), any())).thenReturn(getRequest(dt));
        when(userService.findById(USER_ID)).thenReturn(getAnotherUser());

        mvc.perform(getPostReq(REQUEST_DEFAULT_PATH, USER_ID)
                        .content(mapper.writeValueAsString(getRequestDto(dt))))
                .andExpect(OK)
                .andExpect(content().json(mapper.writeValueAsString(getOutputRequestDto(dt))));
    }
}
