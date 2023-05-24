package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestDao extends JpaRepository<ItemRequest, Long> {
    Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    List<ItemRequest> findByRequesterId(Long creatorId, Sort sort);

    Page<ItemRequest> findAllByRequesterIdIsNot(Long userId, Pageable pageable);

}
