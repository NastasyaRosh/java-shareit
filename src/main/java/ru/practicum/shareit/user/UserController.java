package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.booking.service.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.debug("Создание пользователя.");
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.debug("Обновление информации о пользователе с идентификатором: " + userId);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.updateUser(userId, user));
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.debug("Получение пользователя c идентификатором: " + userId);
        return UserMapper.toUserDto(userService.findById(userId));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Получение списка всех пользователей.");
        return UserMapper.listToUserDto(userService.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Удаление пользователя с идентификатором: " + userId);
        userService.deleteUser(userId);
    }
}
