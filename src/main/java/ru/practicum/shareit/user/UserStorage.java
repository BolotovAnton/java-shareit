package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(Integer userId, User user);

    User findUserById(Integer userId);

    List<User> findAllUsers();

    void deleteUserById(int userId);
}
