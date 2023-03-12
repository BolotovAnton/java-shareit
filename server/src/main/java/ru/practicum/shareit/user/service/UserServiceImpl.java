package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
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
        validation.validateUserId(userId);
        if (userDto.getEmail() == null) {
            userDto.setEmail(userRepository.findById(userId).orElseThrow().getEmail());
        }
        if (userDto.getName() == null) {
            userDto.setName(userRepository.findById(userId).orElseThrow().getName());
        }
        User user = UserMapper.mapToUser(userDto);
        user.setId(userId);
        userRepository.save(user);
        log.info("user has been updated");
        return UserMapper.mapToUserDto(user);
    }

    public UserDto findUserById(Integer userId) {
        validation.validateUserId(userId);
        UserDto userDto = UserMapper.mapToUserDto(userRepository.findById(userId).orElseThrow());
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
        validation.validateUserId(userId);
        userRepository.deleteById(userId);
        log.info("user with id={} has been deleted", userId);
    }
}
