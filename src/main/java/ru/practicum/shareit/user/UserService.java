package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto postUser(UserCreateRequest user) {
        log.debug("Запрос на создание пользователя");
        User newUser = userStorage.postUser(user);
        log.debug("Пользователь с id {} создан", newUser.getId());
        return UserMapper.mapToUserDto(newUser);
    }

    public Collection<UserDto> getUsers() {
        log.debug("Запрос на получение всех пользователей");
        return userStorage.getUsers().stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto getUserById(Long id) {
        log.debug("Запрос на получение пользователя с id {}", id);
        return UserMapper.mapToUserDto(userStorage.getUserById(id));
    }

    public UserDto updateUser(Long userId, UserUpdateRequest newUser) {
        log.debug("Запрос на обновление пользователя с id {}", userId);
        return UserMapper.mapToUserDto(userStorage.updateUser(userId, newUser));
    }

    public void deleteUser(Long id) {
        log.debug("Запрос на удаление пользователя с id {}", id);
        userStorage.deleteUser(id);
    }

}
