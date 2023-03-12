package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable Integer userId) {
        return userClient.findUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        return userClient.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Integer userId) {
        userClient.deleteUserById(userId);
    }
}
