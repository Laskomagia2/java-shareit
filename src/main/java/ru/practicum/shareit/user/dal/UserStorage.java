package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User postUser(UserCreateRequest user);

    Collection<User> getUsers();

    User getUserById(Long id);

    User updateUser(Long userId, UserUpdateRequest newUser);

    void deleteUser(Long id);

}
