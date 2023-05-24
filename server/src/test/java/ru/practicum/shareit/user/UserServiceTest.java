package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.UserTestUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.util.UserTestUtil.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserDao userRep;
    UserService userService;

    private final User firstUser = UserTestUtil.getUser();
    private final User updatedUser = UserTestUtil.getUpdatedUser();

    @BeforeEach
    void beforeEach() {
        userService = new UserService(userRep);
    }

    @Test
    void shouldReturnUsersListWhenCallGetAllUsers() {
        when(userRep.findAll()).thenReturn(List.of(firstUser));

        List<User> users = userService.getAllUsers();
        verify(userRep, times(1)).findAll();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(firstUser, users.get(0));
    }

    @Test
    void shouldReturnUserOrThrowWhenCallFindById() {
        when(userRep.findById(USER_ID)).thenReturn(Optional.of(firstUser));
        when(userRep.findById(ANOTHER_USER_ID)).thenReturn(Optional.empty());

        assertEquals(firstUser, userService.findById(USER_ID));
        assertThrows(EntityNotFoundException.class, () -> userService.findById(ANOTHER_USER_ID));
    }

    @Test
    void shouldCreateAndReturnUserWhenCallCreate() {
        when(userRep.save(firstUser)).thenAnswer(returnsFirstArg());

        assertEquals(firstUser, userService.createUser(firstUser));
        verify(userRep, times(1)).save(firstUser);
        verifyNoMoreInteractions(userRep);

    }

    @Test
    void shouldUpdateAndReturnUserWhenCallUpdate() {
        when(userRep.findById(USER_ID)).thenReturn(Optional.of(firstUser));
        when(userRep.findById(ANOTHER_USER_ID)).thenReturn(Optional.empty());

        assertEquals(firstUser, userService.updateUser(USER_ID, updatedUser));
        verify(userRep, times(1)).findById(USER_ID);
        assertEquals(ANOTHER_USER_NAME, userService.findById(USER_ID).getName());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(ANOTHER_USER_ID, updatedUser));
    }

    @Test
    void shouldDeleteUserWhenCallDeleteUser() {
        userService.deleteUser(USER_ID);
        verify(userRep, times(1)).deleteById(USER_ID);
    }

}

