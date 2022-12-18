package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserEmailValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Validation;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserDto addUser(UserDto userDto) throws UserEmailValidationException {
        Validation.validateUserEmail(userStorage, userDto.getEmail());
        User user = userStorage.addUser(UserMapper.mapToUser(userDto));
        log.info("user has been added");
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(Integer userId, UserDto userDto) throws UserEmailValidationException, ValidationException {
        Validation.validateUserEmail(userStorage, userDto.getEmail());
        Validation.validateUserId(userStorage, userId);
        if (userDto.getEmail() == null) {
            userDto.setEmail(userStorage.findUserById(userId).getEmail());
        }
        if (userDto.getName() == null) {
            userDto.setName(userStorage.findUserById(userId).getName());
        }
        User user = userStorage.updateUser(userId, UserMapper.mapToUser(userDto));
        log.info("user has been updated");
        return UserMapper.mapToUserDto(user);
    }

    public UserDto findUserById(Integer userId) throws ValidationException {
        Validation.validateUserId(userStorage, userId);
        UserDto userDto = UserMapper.mapToUserDto(userStorage.findUserById(userId));
        log.info("user with id={} has been found", userId);
        return userDto;
    }

    public List<UserDto> findAllUsers() {
        List<UserDto> userDtoList = UserMapper.mapToUserDto(userStorage.findAllUsers());
        log.info("amount of users: {}", userStorage.findAllUsers().size());
        return userDtoList;
    }

    public void deleteUserById(int userId) throws ValidationException {
        Validation.validateUserId(userStorage, userId);
        userStorage.deleteUserById(userId);
        log.info("user with id={} has been deleted", userId);
    }
}
