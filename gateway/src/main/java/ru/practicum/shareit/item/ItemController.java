package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static java.util.Collections.emptyList;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "1") Long from,
            @Positive @RequestParam(defaultValue = "30") Integer size
    ) {
        log.debug("GET: Get all items where owner ID = {}.", userId);
        return itemClient.findAllByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        log.debug("GET: Get item by ID = {}.", itemId);
        return itemClient.findById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByParams(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "") String text,
            @PositiveOrZero @RequestParam(defaultValue = "1") Long from,
            @Positive @RequestParam(defaultValue = "30") Integer size
    ) {
        log.debug("GET: Search item containing text '{}' in title or description.", text);
        if (text.isBlank()) {
            return ResponseEntity.ok(emptyList());
        }
        return itemClient.searchByParams(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.debug("Create item {} with owner ID = {}.", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.debug("Update item {} where owner ID = {}.", itemDto, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        log.debug("Delete item with ID = {} where owner ID = {}.", itemId, userId);
        return itemClient.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CommentDto commentDto,
            @PathVariable Long itemId
    ) {
        log.debug("Create comment {} for item ID = {}.", commentDto, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
