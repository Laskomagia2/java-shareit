package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserStorage implements UserStorage{

    private final Map<Long, User> users = new ConcurrentHashMap<>();;
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public User postUser(UserCreateRequest request) {
        validateEmail(request.getEmail(), null);
        User user = UserMapper.mapToUser(request, idIncrement());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public User updateUser(Long userId, UserUpdateRequest newUser) {
        if (users.containsKey(userId)) {
            User oldUser = users.get(userId);
            String updatedName = (newUser.getName() != null)
                    ? newUser.getName()
                    : oldUser.getName();

            String updatedEmail;
            if (newUser.getEmail() != null) {
                validateEmail(newUser.getEmail(), userId);
                updatedEmail = newUser.getEmail();
            } else {
                updatedEmail = oldUser.getEmail();
            }

            User updatedUser = User.builder()
                    .id(userId)
                    .name(updatedName)
                    .email(updatedEmail)
                    .build();

            users.put(userId, updatedUser);

            return updatedUser;
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (users.remove(id) == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    private Long idIncrement() {
        return idGenerator.incrementAndGet();
    }

    private void validateEmail(String email, Long currentUserId) {
        boolean exists = users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)
                        && !user.getId().equals(currentUserId));

        if (exists) {
            throw new DataConflictException("Пользователь с email " + email + " уже существует");
        }
    }
}
