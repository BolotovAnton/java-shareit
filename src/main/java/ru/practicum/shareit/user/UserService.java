package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Integer userId, UserDto userDto);

    UserDto findUserById(Integer userId);

    List<UserDto> findAllUsers();

    void deleteUserById(Integer userId);
}
