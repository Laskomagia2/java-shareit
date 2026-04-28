package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userStorage;

    @Transactional(rollbackFor = Exception.class)
    public UserDto postUser(UserCreateRequest req) {
        log.debug("Запрос на создание пользователя");
        User newUser = userStorage.save(UserMapper.mapToUser(req));
        log.debug("Пользователь с id {} создан", newUser.getId());
        return UserMapper.mapToUserDto(newUser);
    }

    public Collection<UserDto> getUsers() {
        log.debug("Запрос на получение всех пользователей");
        return userStorage.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto getUserById(Long id) {
        log.debug("Запрос на получение пользователя с id {}", id);
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    public UserDto updateUser(Long userId, UserUpdateRequest newUser) {
        log.debug("Запрос на обновление пользователя с id {}", userId);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            user.setEmail(newUser.getEmail());
        }

        User updatedUser = userStorage.save(user);

        return UserMapper.mapToUserDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.debug("Запрос на удаление пользователя с id {}", id);
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userStorage.deleteById(id);
    }

}
