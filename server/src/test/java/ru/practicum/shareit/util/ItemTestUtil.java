package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.InItemDto;
import ru.practicum.shareit.item.dto.OutItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemTestUtil {
    public static final String ITEM_DEFAULT_PATH = "/items";
    public static final Long ITEM_ID = 1L;
    public static final String ITEM_NAME = "firstItem";
    public static final String ITEM_DESCR = "firstItemDescr";
    public static final Long ANOTHER_ITEM_ID = 2L;
    public static final String ANOTHER_ITEM_DESCR = "anotherItemDescr";
    public static final String ITEM_PATH = ITEM_DEFAULT_PATH + "/" + ITEM_ID;

    public static Item getItem() {
        return Item.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCR)
                .available(true)
                .owner(UserTestUtil.getUser())
                .build();
    }

    public static Item getUpdatedItem() {
        return Item.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ANOTHER_ITEM_DESCR)
                .available(false)
                .owner(UserTestUtil.getUser())
                .build();
    }

    public static InItemDto getInputItemDto() {
        return InItemDto.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCR)
                .available(true)
                .build();
    }

    public static OutItemDto getOutDto(Long userId) {
        return ItemMapper.toItemDto(getItem(), userId);
    }

    public static Item getNewItem(User owner) {
        return Item.builder()
                .name(ITEM_NAME)
                .description(ITEM_DESCR)
                .available(true)
                .owner(owner)
                .build();
    }

    public static List<Item> getListItemsWithRequest() {
        List<Item> items = new ArrayList<>();
        items.add(getItem());
        items.get(0).setRequest(RequestTestUtil.getRequest(LocalDateTime.now()));
        return items;
    }
}

