package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findRequestsOfUser(Integer userId, PageRequest pageRequest);

    List<ItemRequestDto> findAllRequests(Integer userId, PageRequest pageRequest);

    ItemRequestDto findById(Integer userId, Integer requestId);
}
