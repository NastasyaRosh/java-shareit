package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final Logger log = LoggerFactory.getLogger(ItemRequestController.class);

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("x-sharer-user-id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Создание запроса на вещь.");
        return ItemRequestMapper.toItemRequestDto(itemRequestService.createRequest(itemRequestDto, userId));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("x-sharer-user-id") Long userId, @PathVariable Long requestId) {
        log.debug("Получение данных об одном конкретном запросе.");
        return ItemRequestMapper.toItemRequestDto(itemRequestService.getItemRequestById(userId, requestId));
    }

    @GetMapping
    public List<ItemRequestDto> getAllMyItemRequests(@RequestHeader("x-sharer-user-id") Long userId) {
        log.debug("Получение списка своих запросов.");
        return ItemRequestMapper.mapToRequestDto(itemRequestService.getAllMyItemRequests(userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("x-sharer-user-id") Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получение списка запросов, созданных другими пользователями.");
        return ItemRequestMapper.mapToRequestDto(itemRequestService.getAllRequests(userId, from, size));
    }

}
