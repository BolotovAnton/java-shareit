package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.UserEmailValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto) throws UserEmailValidationException;

    UserDto updateUser(Integer userId, UserDto userDto) throws UserEmailValidationException, ValidationException;

    UserDto findUserById(Integer userId) throws ValidationException;

    List<UserDto> findAllUsers() throws ValidationException;

    void deleteUserById(int userId) throws ValidationException;
}
