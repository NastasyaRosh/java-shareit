package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.TestUtil;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

import static ru.practicum.shareit.util.TestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;

    User user;
    User updatedUser;
    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = getUser();
        updatedUser = getUpdatedUser();
        userDto = getUserDto();
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(emptyList());

        mvc.perform(get(USER_DEFAULT_PATH))
                .andExpect(content().contentType(DEFAULT_MEDIA_TYPE))
                .andExpect(OK)
                .andExpect(content().json(mapper.writeValueAsString(emptyList())));
    }

    @Test
    void findById() throws Exception {
        when(userService.findById(USER_ID)).thenReturn(user);
        when(userService.findById(ANOTHER_USER_ID))
                .thenThrow(new EntityNotFoundException(User.class.getSimpleName()));

        mvc.perform(TestUtil.getGetReq(USER_PATH, null))
                .andExpect(OK)
                .andExpect(content().contentType(DEFAULT_MEDIA_TYPE))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class));

        mvc.perform(TestUtil.getGetReq(ANOTHER_USER_PATH, null))
                .andExpect(NOT_FOUND);
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any())).thenReturn(user);

        mvc.perform(getPostReq(USER_DEFAULT_PATH, null)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(OK)
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any(), any())).thenReturn(updatedUser);

        mvc.perform(getPatchReq(USER_PATH, null)
                        .content(mapper.writeValueAsString(getUpdatedUserDto())))
                .andExpect(OK)
                .andExpect(content().contentType(DEFAULT_MEDIA_TYPE))
                .andExpect(content().json(mapper.writeValueAsString(updatedUser)));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete(USER_PATH))
                .andExpect(OK);
    }
}
