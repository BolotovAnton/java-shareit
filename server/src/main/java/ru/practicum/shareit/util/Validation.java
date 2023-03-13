package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class Validation {

    private final UserRepository userRepository;

    public User validateUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ValidationException("user with id " + userId + " not found")
        );
    }
}
