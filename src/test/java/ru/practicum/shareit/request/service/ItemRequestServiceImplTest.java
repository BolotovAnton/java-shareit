package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {

    Validation validation;
    ItemRequestMapper itemRequestMapper;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRequestService itemRequestService;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    User user;

    @BeforeEach
    void setUp() {
        validation = mock(Validation.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestMapper = new ItemRequestMapper(userRepository);
        itemRequestService = new ItemRequestServiceImpl(
                validation,
                itemRequestMapper,
                itemRequestRepository,
                userRepository
        );

        user = new User(1, "user", "user@email.com", Collections.emptySet());

        itemRequest = new ItemRequest(
                1,
                "itemrequest",
                user,
                LocalDateTime.now(),
                Collections.emptySet()
        );

        itemRequestDto = new ItemRequestDto(
                1,
                "itemrequest",
                LocalDateTime.now(),
                Collections.emptySet()
        );
    }

    @Test
    void addRequest() {

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto responseItemRequestDto = itemRequestService.addRequest(user.getId(), itemRequestDto);

        assertNotNull(responseItemRequestDto);
        assertEquals(itemRequest.getId(), responseItemRequestDto.getId());
    }

    @Test
    void findRequestsOfUser_whenRequestsOfUserIsNotEmpty_thenItemRequestDtoListReturned() {

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        final PageImpl<ItemRequest> itemRequestPage = new PageImpl<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findItemRequestByRequesterId(anyInt(), any())).thenReturn(itemRequestPage);
        user.setRequests(Set.of(itemRequest));

        List<ItemRequestDto> requestsOfUser = itemRequestService.findRequestsOfUser(
                user.getId(),
                PageRequest.ofSize(10)
        );

        assertEquals(1, requestsOfUser.size());
        assertEquals(itemRequest.getId(), requestsOfUser.get(0).getId());
    }

    @Test
    void findRequestsOfUser_whenRequestsOfUserIsEmpty_thenEmptyListReturned() {

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        final PageImpl<ItemRequest> itemRequestPage = new PageImpl<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findItemRequestByRequesterId(anyInt(), any())).thenReturn(itemRequestPage);

        List<ItemRequestDto> requestsOfUser = itemRequestService.findRequestsOfUser(
                user.getId(),
                PageRequest.ofSize(10)
        );

        assertTrue(requestsOfUser.isEmpty());
    }

    @Test
    void findAllRequests() {

        final PageImpl<ItemRequest> itemRequestPage = new PageImpl<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findItemRequestByRequesterIdIsNot(anyInt(), any())).thenReturn(itemRequestPage);

        List<ItemRequestDto> requestsOfUser = itemRequestService.findAllRequests(
                user.getId(),
                PageRequest.ofSize(10)
        );

        assertEquals(1, requestsOfUser.size());
        assertEquals(itemRequest.getId(), requestsOfUser.get(0).getId());
    }

    @Test
    void findById() {

        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto responseItemRequestDto = itemRequestService.findById(user.getId(), itemRequest.getId());

        assertNotNull(responseItemRequestDto);
        assertEquals(itemRequest.getId(), responseItemRequestDto.getId());
    }
}