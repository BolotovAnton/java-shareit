package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.ItemAvailableValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Integer userId, UserDto userDto) throws ItemAvailableValidationException, ValidationException;

    UserDto findUserById(Integer userId) throws ValidationException;

    List<UserDto> findAllUsers() throws ValidationException;

    void deleteUserById(Integer userId) throws ValidationException;
}
