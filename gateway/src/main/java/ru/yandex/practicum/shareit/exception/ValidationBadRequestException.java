package ru.yandex.practicum.shareit.exception;

public class ValidationBadRequestException extends RuntimeException {
    public ValidationBadRequestException(String message) {
        super(message);
    }
}
