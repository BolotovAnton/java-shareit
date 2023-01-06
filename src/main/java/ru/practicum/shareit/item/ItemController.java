package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                @PathVariable Integer itemId) {
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAllItemsForUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.findAllItemsForUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam String text) {
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
