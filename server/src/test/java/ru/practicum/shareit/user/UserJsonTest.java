package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.UserTestUtil;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.util.UserTestUtil.*;

@JsonTest
public class UserJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;
    private final UserDto userDto = UserTestUtil.getUserDto();

    @Test
    void userDtoSerialize() throws IOException {
        userDto.setId(USER_ID);

        JsonContent<UserDto> jsonUserDto = json.write(userDto);
        assertThat(jsonUserDto).extractingJsonPathNumberValue("$.id").isEqualTo(USER_ID.intValue());
        assertThat(jsonUserDto).extractingJsonPathStringValue("$.email").isEqualTo(USER_EMAIL);
        assertThat(jsonUserDto).extractingJsonPathStringValue("$.name").isEqualTo(USER_NAME);

        UserDto parsed = json.parse(jsonUserDto.getJson()).getObject();
        assertEquals(userDto, parsed);
    }
}

