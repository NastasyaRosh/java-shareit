package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemDao itemRepository;
    private final UserService userService;

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        checkCreationRequest(itemDto);
        Item item = installOwner(itemDto, userId);
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        validationUpdatingItem(itemId, userId);
        setFieldsInUpdatingItem(itemDto, itemId);
        Item item = installOwner(itemDto, userId);
        return ItemMapper.toItemDto(itemRepository.updateItem(item, itemId, userId));
    }

    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.getItem(itemId).orElseThrow(() -> new EntityNotFoundException("Запрашиваемой вещи не существует."));
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getAllMyItems(Long userId) {
        return itemRepository.getAllMyItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void checkCreationRequest(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null
                || itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Передано пустое имя или описание.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не передано значение доступности.");
        }
    }

    private Item installOwner(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.getUser(userId)));
        return item;
    }

    private void setFieldsInUpdatingItem(ItemDto itemDto, Long itemId) {
        if (itemDto.getName() == null) {
            itemDto.setName(getItem(itemId).getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(getItem(itemId).getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(getItem(itemId).getAvailable());
        }
    }

    private void validationUpdatingItem(Long itemId, Long userId) {
        if (!(getAllMyItems(userId).contains(getItem(itemId)))) {
            throw new AccessException(String.format("Пользователь с id = %s не имеет доступа к вещи с id = %s"
                    , userId, itemId));
        }
    }
}