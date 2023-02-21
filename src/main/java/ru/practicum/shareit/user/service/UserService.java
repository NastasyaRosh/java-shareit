package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserAlreadyExist;
import ru.practicum.shareit.exceptions.UserOrItemNotExist;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userRepository;

    public UserDto createUser(UserDto userDto) {
        checkCreationUser(userDto);
        User user = userRepository.createUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        checkUpdatingUser(userId, userDto);
        setFieldsInUpdatingUser(userId, userDto);
        User user = userRepository.updateUser(userId, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto getUser(Long userId) {
        User user = userRepository.getUser(userId).orElseThrow(() -> new UserOrItemNotExist("Запрашиваемого пользователя не существует."));
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    private boolean isEmail(String email) {
        return email.contains("@");
    }

    private void checkCreationUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null || !isEmail(userDto.getEmail())) {
            throw new ValidationException("Передано пустое имя или пустая/неверная почта.");
        }
        if (userRepository.checkEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExist(String.format("Пользователь с почтой %s уже существует.", userDto.getEmail()));
        }
    }

    private void setFieldsInUpdatingUser(Long userId, UserDto userDto) {
        if (userDto.getName() == null) {
            userDto.setName(getUser(userId).getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(getUser(userId).getEmail());
        }
    }

    private void checkUpdatingUser(Long userId, UserDto userDto) {
        Optional<User> sameEmailUser = userRepository.checkEmail(userDto.getEmail());
        if (sameEmailUser.isPresent() && (sameEmailUser.get().getId() != userId)) {
            throw new UserAlreadyExist(String.format("Пользователь с почтой %s уже существует.", userDto.getEmail()));
        }
    }
}
