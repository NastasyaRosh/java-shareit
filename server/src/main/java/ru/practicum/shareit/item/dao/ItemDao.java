package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemDao extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Sort sort);

    @Query("select i from Item as i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> searchItems(String text);

    List<Item> findAllByRequestIn(List<ItemRequest> requests);

    List<Item> findAllByRequestId(Long requestId);
}
