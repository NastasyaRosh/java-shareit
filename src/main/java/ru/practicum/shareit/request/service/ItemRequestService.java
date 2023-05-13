package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestDao itemRequestRepository;
    private final UserService userService;
    private final ItemDao itemRepository;

    @Transactional
    public ItemRequest createRequest(ItemRequestDto itemRequestDto, Long userId) {
        checkNewRequest(itemRequestDto);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(userService.findById(userId));
        itemRequestRepository.save(itemRequest);
        itemRequest.setItems(itemRepository.findAllByRequestId(itemRequest.getId()));
        return itemRequest;
    }

    public ItemRequest getItemRequestById(Long userId, Long requestId) {
        userService.findById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Запроса с id = %d не существует.", requestId)));
        itemRequest.setItems(itemRepository.findAllByRequestId(requestId));
        return itemRequest;
    }

    public List<ItemRequest> getAllMyItemRequests(Long userId) {
        userService.findById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(userId, itemRequestRepository.CREATED_DESC);
        return setItems(itemRequests);
    }

    public List<ItemRequest> getAllRequests(Long userId, Integer from, Integer size) {
        checkPageableParams(from, size);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdIsNot(userId, pageable).getContent();
        return setItems(requests);
    }

    private void checkNewRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("Введите непустой запрос.");
        }
    }

    private void checkPageableParams(Integer from, Integer size) {
        if ((from < 0) || (size <= 0)) {
            throw new ValidationException("Введите верные данные для пагинации.");
        }
    }

    private List<ItemRequest> setItems(List<ItemRequest> requests) {
        Map<ItemRequest, List<Item>> items = itemRepository.findAllByRequestIn(requests).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Item::getRequest, Collectors.toList()));
        for (ItemRequest request : requests) {
            request.setItems(items.getOrDefault(request, List.of()));
        }
        return requests;
    }
}
