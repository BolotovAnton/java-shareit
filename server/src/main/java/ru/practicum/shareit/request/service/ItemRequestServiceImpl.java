package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.util.Validation;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final Validation validation;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addRequest(Integer userId, ItemRequestDto itemRequestDto) {
        validation.validateUser(userId);
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(itemRequestDto, userId);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        log.info("itemrequest has been added");
        return itemRequestMapper.mapToItemRequestDto(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findRequestsOfUser(Integer userId, PageRequest pageRequest) {
        validation.validateUser(userId);
        if (userRepository.findById(userId).orElseThrow().getRequests().isEmpty()) {
            return Collections.emptyList();
        }
        Page<ItemRequest> itemRequestList = itemRequestRepository.findItemRequestByRequesterId(
                userId, pageRequest);
        log.info("requests for user with id={} have been found", userId);
        return itemRequestMapper.mapToItemRequestDto(itemRequestList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findAllRequests(Integer userId, PageRequest pageRequest) {
        validation.validateUser(userId);
        Page<ItemRequest> itemRequestList = itemRequestRepository.findItemRequestByRequesterIdIsNot(
                userId, pageRequest);
        log.info("all requests without requests for user with id={} have been found", userId);
        return itemRequestMapper.mapToItemRequestDto(itemRequestList);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto findById(Integer userId, Integer requestId) {
        validation.validateUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ValidationException("request with id " + requestId + " not found")
        );
        log.info("request with id={} has been found", requestId);
        return itemRequestMapper.mapToItemRequestDto(itemRequest);
    }
}
