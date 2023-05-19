package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.util.RequestTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;

@JsonTest
public class RequestJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jsonInput;
    private final LocalDateTime dt = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ItemRequestDto requestDto = getRequestDto(dt);

    @Test
    void requestDtoTest() throws IOException {
        requestDto.setId(REQUEST_ID);
        requestDto.setRequester(UserMapper.toUserDto(getUser()));

        JsonContent<ItemRequestDto> jsonDto = jsonInput.write(requestDto);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(REQUEST_ID.intValue());
        assertThat(jsonDto).extractingJsonPathStringValue("$.description").isEqualTo(REQUEST_DESCR);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.requester.id").isEqualTo(USER_ID.intValue());
        assertThat(jsonDto).extractingJsonPathStringValue("$.created").isEqualTo(dt.format(formatter));
        assertThat(jsonDto).extractingJsonPathNumberValue("$.items.length()").isEqualTo(1);

        ItemRequestDto parsed = jsonInput.parse(jsonDto.getJson()).getObject();
        assertEquals(requestDto, parsed);
    }
}
