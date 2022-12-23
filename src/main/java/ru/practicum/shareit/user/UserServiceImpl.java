package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Validation;

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
        userRepository.save(user);
        log.info("user has been added");
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    public UserDto updateUser(Integer userId, UserDto userDto) throws ValidationException {
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

    public UserDto findUserById(Integer userId) throws ValidationException {
        validation.validateUserId(userId);
        UserDto userDto = UserMapper.mapToUserDto(userRepository.findById(userId).orElseThrow());
        log.info("user with id={} has been found", userId);
        return userDto;
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        List<UserDto> userDtoList = UserMapper.mapToUserDto(userRepository.findAll());
        log.info("amount of users: {}", userRepository.findAll().size());
        return userDtoList;
    }

    @Transactional
    public void deleteUserById(Integer userId) throws ValidationException {
        validation.validateUserId(userId);
        userRepository.deleteById(userId);
        log.info("user with id={} has been deleted", userId);
    }
}
