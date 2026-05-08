package ru.yandex.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateRequest {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

}
