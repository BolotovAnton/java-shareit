package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ItemAvailableValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) throws ValidationException, ItemAvailableValidationException {
        return userService.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) throws ValidationException {
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() throws ValidationException {
        return userService.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Integer userId) throws ValidationException {
        userService.deleteUserById(userId);
    }
}
