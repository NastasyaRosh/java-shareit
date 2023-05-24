package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InItemDto;
import ru.practicum.shareit.item.dto.OutItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @PostMapping
    public OutItemDto createItem(@RequestHeader("x-sharer-user-id") Long userId, @RequestBody InItemDto inItemDto) {
        log.debug("Создание вещи.");
        return ItemMapper.toItemDto(itemService.createItem(inItemDto, userId), userId);
    }

    @PatchMapping("/{itemId}")
    public OutItemDto updateItem(@RequestHeader("x-sharer-user-id") Long userId,
                                 @RequestBody InItemDto inItemDto, @PathVariable Long itemId) {
        log.debug("Обновление информации о вещи с идентификатором: " + itemId);
        return ItemMapper.toItemDto(itemService.updateItem(ItemMapper.toItem(inItemDto), itemId, userId), userId);
    }

    @GetMapping("/{itemId}")
    public OutItemDto getItem(@RequestHeader("x-sharer-user-id") Long userId, @PathVariable Long itemId) {
        log.debug("Получение вещи c идентификатором: " + itemId);
        return ItemMapper.toItemDto(itemService.getItem(itemId), userId);
    }

    @GetMapping
    public List<OutItemDto> getAllMyItems(@RequestHeader("x-sharer-user-id") Long userId) {
        log.debug("Получение списка всех вещей пользователя с id = " + userId);
        return ItemMapper.listToItemDto(itemService.getAllMyItems(userId), userId);
    }

    @GetMapping("/search")
    public List<OutItemDto> searchItems(@RequestHeader("x-sharer-user-id") Long userId, @RequestParam String text) {
        log.debug("Поиск доступных вещей по строке: " + text);
        return ItemMapper.listToItemDto(itemService.searchItems(text), userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("x-sharer-user-id") Long userId,
                                    @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        log.debug("Создание комментария для вещи с номером: " + itemId);
        return CommentMapper.toCommentDto(itemService.createComment(userId, itemId, commentDto.getText()));
    }

}
