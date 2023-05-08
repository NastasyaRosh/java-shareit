package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserDao userRepository;

    @Transactional
    public User createUser(User user) {
        checkCreationUser(user);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long userId, User user) {
        User oldUser = userRepository.findById(userId).orElseThrow(
                () -> new  EntityNotFoundException("Запрашиваемого пользователя не существует.")
        );
        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }
        return oldUser;
    }

    public User findById (Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Запрашиваемого пользователя не существует."));
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean isEmail(String email) {
        return email.contains("@");
    }

    private void checkCreationUser(User user) {
        if (user.getEmail() == null || user.getName() == null || !isEmail(user.getEmail())) {
            throw new ValidationException("Передано пустое имя или пустая/неверная почта.");
        }
    }
}
