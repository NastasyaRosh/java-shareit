package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private long id;
    private String name;
    private String email;

    @Builder
    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
