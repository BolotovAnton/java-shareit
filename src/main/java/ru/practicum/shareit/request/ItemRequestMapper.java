package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final UserRepository userRepository;

    public ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, Integer userId) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(userRepository.findById(userId).orElseThrow());
        itemRequest.setItems(Collections.emptySet());
        return itemRequest;
    }

    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(new HashSet<>(ItemMapper.mapToItemDto(itemRequest.getItems())));
        return itemRequestDto;
    }

    public List<ItemRequestDto> mapToItemRequestDto(Page<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(mapToItemRequestDto(itemRequest));
        }
        return  itemRequestDtos;
    }
}
