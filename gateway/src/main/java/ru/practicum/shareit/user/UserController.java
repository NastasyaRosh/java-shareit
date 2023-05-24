package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserValidator;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.debug("Get all users.");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable Long userId) {
        log.debug("Get user with ID = {}.", userId);
        return userClient.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated(UserValidator.Create.class) @RequestBody UserDto userDto) {
        log.debug("Create user - {}.", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(
            @PathVariable Long userId,
            @RequestBody @Validated(UserValidator.Update.class) UserDto userDto
    ) {
        log.debug("Update user with ID = {}. New data: {}.", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable Long userId) {
        log.debug("Delete user with  ID = {}.", userId);
        return userClient.deleteById(userId);
    }
}
