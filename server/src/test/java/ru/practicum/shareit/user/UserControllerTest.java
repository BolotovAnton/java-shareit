package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "user", "user@mail.com");
    }

    @Test
    void addUser() throws Exception {

        when(userService.addUser(any())).thenReturn(userDto);
        String body = mapper.writeValueAsString(userDto);

        mockMvc.perform(post("/users").contentType("application/json").content(body)).andExpect(status().isOk()).andExpect(content().json(body));

        verify(userService, times(1)).addUser(any());
    }

    @Test
    void updateUser() throws Exception {

        when(userService.updateUser(anyInt(), any())).thenReturn(userDto);
        String body = mapper.writeValueAsString(userDto);

        mockMvc.perform(patch("/users/1").contentType("application/json").content(body)).andExpect(status().isOk());

        verify(userService, times(1)).updateUser(anyInt(), any());
    }

    @Test
    void getUserById() throws Exception {

        when(userService.findUserById(anyInt())).thenReturn(userDto);
        String body = mapper.writeValueAsString(userDto);

        mockMvc.perform(get("/users/1")).andExpect(status().isOk()).andExpect(content().json(body));

        verify(userService, times(1)).findUserById(anyInt());
    }

    @Test
    void findAllUsers() throws Exception {

        when(userService.findAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(content().json("[]"));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void deleteUserById() throws Exception {

        this.mockMvc.perform(
                        delete("/users/1")
                )
                .andExpect(status().isOk());
    }
}