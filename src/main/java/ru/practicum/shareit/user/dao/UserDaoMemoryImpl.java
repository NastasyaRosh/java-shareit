package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDaoMemoryImpl implements UserDao {
    private Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User createUser(User user) {
        users.put(setId(user), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(Long userId, User user) {
        user.setId(userId);
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private long setId(User user) {
        user.setId(++this.id);
        return this.id;
    }

    public Optional<User> checkEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }
}
