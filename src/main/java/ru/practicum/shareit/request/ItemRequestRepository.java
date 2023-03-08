package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    Page<ItemRequest> findItemRequestByRequesterId(Integer userId, PageRequest pageRequest);

    Page<ItemRequest> findItemRequestByRequesterIdIsNot(Integer userId, PageRequest pageRequest);
}
