package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private static int generateId = 1;

    private final HashMap<Integer, User> users = new HashMap<>();

    private static Integer getNextId() {
        return generateId++;
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Integer userId, User user) {
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User findUserById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(int userId) {
        users.remove(userId);
    }
}
