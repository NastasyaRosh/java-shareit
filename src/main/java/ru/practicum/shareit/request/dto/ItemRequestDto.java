package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;
    private String description;

    @Builder
    public ItemRequestDto(Long id, String description) {
        this.id = id;
        this.description = description;
    }
}
