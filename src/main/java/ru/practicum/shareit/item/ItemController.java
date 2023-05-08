package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @PostMapping
    public ItemDto createItem(@RequestHeader("x-sharer-user-id") Long userId, @RequestBody ItemDto itemDto) {
        log.debug("Создание вещи.");
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("x-sharer-user-id") Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.debug("Обновление информации о вещи с идентификатором: " + itemId);
        return ItemMapper.toItemDto(itemService.updateItem(ItemMapper.toItem(itemDto), itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.debug("Получение вещи c идентификатором: " + itemId);
        return ItemMapper.toItemDto(itemService.getItem(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllMyItems(@RequestHeader("x-sharer-user-id") Long userId) {
        log.debug("Получение списка всех вещей пользователя с id = " + userId);
        return ItemMapper.listToItemDto(itemService.getAllMyItems(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.debug("Поиск доступных вещей по строке: " + text);
        return ItemMapper.listToItemDto(itemService.searchItems(text));
    }

}
