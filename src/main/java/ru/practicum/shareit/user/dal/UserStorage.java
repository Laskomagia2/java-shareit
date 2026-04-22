package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    public User postUser(UserCreateRequest user);
    public Collection<User> getUsers();
    public User getUserById(Long id);
    public User updateUser(Long userId, UserUpdateRequest newUser);
    public void deleteUser(Long id);

}
