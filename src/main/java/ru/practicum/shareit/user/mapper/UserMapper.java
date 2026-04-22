package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public final class UserMapper {

    public User mapToUser(UserCreateRequest request, Long id) {
        return User.builder()
                .id(id)
                .email(request.getEmail())
                .name(request.getName())
                .build();
    }

    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
