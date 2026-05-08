package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email",
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @NotNull(message = "Имя не может быть пустым")
    private String name;
}
