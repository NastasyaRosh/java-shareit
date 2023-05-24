package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private Long id;
    @NotBlank(groups = UserValidator.Create.class)
    @Email(groups = {UserValidator.Create.class, UserValidator.Update.class})
    private String email;
    @NotBlank(groups = UserValidator.Create.class)
    private String name;
}
