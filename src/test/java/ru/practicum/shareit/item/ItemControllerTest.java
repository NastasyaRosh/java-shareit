package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.util.CommentTestUtil.*;
import static ru.practicum.shareit.util.ItemTestUtil.*;
import static ru.practicum.shareit.util.TestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.USER_ID;
import static ru.practicum.shareit.util.UserTestUtil.getUser;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;
    @MockBean
    ItemService itemService;
    @MockBean
    ItemRequestService requestService;

    @Test
    void findAllByUserId() throws Exception {
        when(itemService.getAllMyItems(USER_ID)).thenReturn(List.of(getItem()));

        mvc.perform(getGetReq(ITEM_DEFAULT_PATH, USER_ID))
                .andExpect(OK)
                .andExpect(content()
                        .json(mapper.writeValueAsString(List.of(ItemMapper.toItemDto(getItem(), USER_ID)))));
    }

    @Test
    void findById() throws Exception {
        when(itemService.getItem(ITEM_ID)).thenReturn(getItem());

        mvc.perform(getGetReq(ITEM_PATH, USER_ID))
                .andExpect(OK)
                .andExpect(jsonPath("$.id", is(getItem().getId()), Long.class));
    }

    @Test
    void searchByParams() throws Exception {
        when(itemService.searchItems("escr")).thenReturn(List.of(getItem()));

        mvc.perform(getGetReq(ITEM_DEFAULT_PATH + "/search?text=escr", ANOTHER_ITEM_ID))
                .andExpect(OK)
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void create() throws Exception {
        when(itemService.createItem(any(), any())).thenReturn(getItem());

        mvc.perform(getPostReq(ITEM_DEFAULT_PATH, USER_ID)
                        .content(mapper.writeValueAsString(getInputItemDto())))
                .andExpect(OK)
                .andExpect(content().json(mapper.writeValueAsString(getOutDto(USER_ID))));
    }

    @Test
    void update() throws Exception {
        when(itemService.updateItem(any(), any(), any())).thenReturn(getUpdatedItem());

        mvc.perform(getPatchReq(ITEM_PATH, USER_ID)
                        .content(mapper.writeValueAsString(getInputItemDto())))
                .andExpect(OK)
                .andExpect(content()
                        .json(mapper.writeValueAsString(ItemMapper.toItemDto(getUpdatedItem(), USER_ID))));
    }

    @Test
    void createComment() throws Exception {
        when(userService.findById(USER_ID)).thenReturn(getUser());
        when(itemService.getItem(ITEM_ID)).thenReturn(getItem());
        when(itemService.createComment(any(), any(), any())).thenReturn(getComment());

        mvc.perform(getPostReq(ITEM_PATH + "/comment", USER_ID)
                        .content(mapper.writeValueAsString(getCommentDto())))
                .andExpect(OK)
                .andExpect(jsonPath("$.id",
                        is(getOutputCommentDto().getId()), Long.class));
    }
}
