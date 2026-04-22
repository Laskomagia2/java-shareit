package ru.practicum.shareit.booking.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Status {
    WAITING(1, "WAITING"),
    APPROVED(2, "APPROVED"),
    REJECTED(3, "REJECTED"),
    CANCELED(4, "CANCELED");

    private final int id;
    private final String name;

    Status(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Status fromId(int id) {
        for (Status type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Неизвестный ID статуса: " + id);
    }

    @JsonValue
    @Override
    public String toString() {
        return name;
    }
}
