package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validation;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Validation validation;

    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        log.info("user has been added");
        return UserMapper.mapToUserDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User userFromRepository = validation.validateUser(userId);
        if (userDto.getEmail() == null) {
            userDto.setEmail(userFromRepository.getEmail());
        }
        if (userDto.getName() == null) {
            userDto.setName(userFromRepository.getName());
        }
        User userForUpdate = UserMapper.mapToUser(userDto);
        userForUpdate.setId(userId);
        userRepository.save(userForUpdate);
        log.info("user has been updated");
        return UserMapper.mapToUserDto(userForUpdate);
    }

    public UserDto findUserById(Integer userId) {
        User user = validation.validateUser(userId);
        UserDto userDto = UserMapper.mapToUserDto(user);
        log.info("user with id={} has been found", userId);
        return userDto;
    }

    public List<UserDto> findAllUsers() {
        List<UserDto> userDtoList = UserMapper.mapToUserDto(userRepository.findAll());
        log.info("amount of users: {}", userRepository.findAll().size());
        return userDtoList;
    }

    @Transactional
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
        log.info("user with id={} has been deleted", userId);
    }
}
