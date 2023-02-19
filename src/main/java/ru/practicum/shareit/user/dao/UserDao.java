package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User createUser(User user);

    User updateUser(Long userId, User user);

    Optional<User> getUser(Long userId);

    List<User> getAllUsers();

    void deleteUser(Long userId);

    Optional<User> checkEmail(String email);
}
