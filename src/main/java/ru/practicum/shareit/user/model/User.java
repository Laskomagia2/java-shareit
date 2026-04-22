package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
public class User {
    private final Long id;
    private final String name;
    private final String email;
}
