package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserTestUtil {
    public static final String USER_DEFAULT_PATH = "/users";
    public static final Long USER_ID = 1L;
    public static final String USER_NAME = "first";
    public static final String USER_EMAIL = "first@test.ru";
    public static final Long ANOTHER_USER_ID = 2L;
    public static final String ANOTHER_USER_NAME = "second";
    public static final String ANOTHER_USER_EMAIL = "second@test.ru";
    public static final String USER_PATH = USER_DEFAULT_PATH + "/" + USER_ID;
    public static final String ANOTHER_USER_PATH = USER_DEFAULT_PATH + "/" + ANOTHER_USER_ID;


    public static User getUser() {
        return new User(USER_ID, USER_NAME, USER_EMAIL);
    }

    public static User getUpdatedUser() {
        return new User(USER_ID, ANOTHER_USER_NAME, USER_EMAIL);
    }

    public static User getAnotherUser() {
        return new User(ANOTHER_USER_ID, ANOTHER_USER_NAME, "another@test.com");
    }

    public static UserDto getUserDto() {
        return UserDto.builder().name(USER_NAME).email(USER_EMAIL).build();
    }

    public static UserDto getUpdatedUserDto() {
        return UserDto.builder().name(ANOTHER_USER_NAME).email(USER_EMAIL).build();
    }

    public static User getNewUser() {
        return User.builder().name(USER_NAME).email(USER_EMAIL).build();
    }

    public static User getNewAnotherUser() {
        return User.builder().name(ANOTHER_USER_NAME).email(ANOTHER_USER_EMAIL).build();
    }
}

