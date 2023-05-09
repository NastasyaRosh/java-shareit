package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class InItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    @Builder
    public InItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
