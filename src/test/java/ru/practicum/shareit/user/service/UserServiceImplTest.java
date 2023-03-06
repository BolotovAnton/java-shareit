package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    UserRepository userRepository;
    Validation validation;
    UserService userService;
    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        validation = mock((Validation.class));
        userService = new UserServiceImpl(userRepository, validation);

        user = new User(1, "user", "user@email.com");
        userDto = new UserDto(1, "user", "user@mail.com");
    }

    @Test
    void addUser_whenInvoked_thenSavedUserDtoReturned() {

        when(userRepository.save(any())).thenReturn(user);

        UserDto responseUserDto = userService.addUser(userDto);

        assertNotNull(responseUserDto);
        assertEquals(user.getId(), responseUserDto.getId());
    }

    @Test
    void updateUser_whenNameEmailAreNotNull_thenUserDtoReturned() {

        when(userRepository.save(any())).thenReturn(user);

        UserDto responseUserDto = userService.updateUser(user.getId(), userDto);

        assertNotNull(responseUserDto);
        assertEquals(user.getId(), responseUserDto.getId());
    }

    @Test
    void updateUser_whenNameEmailAreNull_thenUserDtoReturned() {

        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        userDto.setEmail(null);
        userDto.setName(null);

        UserDto responseUserDto = userService.updateUser(user.getId(), userDto);

        assertNotNull(responseUserDto);
        assertEquals(user.getId(), responseUserDto.getId());
    }

    @Test
    void findUserById_whenUserFound_thenUserDtoReturned() {

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        UserDto responseUserDto = userService.findUserById(user.getId());

        assertNotNull(responseUserDto);
        assertEquals(user.getId(), responseUserDto.getId());
    }

    @Test
    void findAllUsers_whenUsersFound_thenUserDtoListReturned() {

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> allUsers = userService.findAllUsers();

        assertEquals(1, allUsers.size());
        assertEquals(user.getId(), allUsers.get(0).getId());
    }

    @Test
    void deleteUserById_whenDeleteUser_thenEmptyListReturned() {

        when(userRepository.save(any())).thenReturn(user);

        Integer id = userService.addUser(userDto).getId();
        userService.deleteUserById(id);
        List<UserDto> allUsers = userService.findAllUsers();
        assertEquals(0, allUsers.size());
    }

    @Test
    void userModelTest() {

        User userTest = new User(1, "usertest", "usertest@mail.com");

        assertTrue(userTest.equals(user));
    }
}