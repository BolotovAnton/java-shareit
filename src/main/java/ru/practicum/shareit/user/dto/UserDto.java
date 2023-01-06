package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class UserDto {

    @Positive
    private Integer id;

    private String name;

    @NotBlank
    @Email
    private String email;
}
