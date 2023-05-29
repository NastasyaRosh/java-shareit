package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentDao extends JpaRepository<Comment, Long> {
    Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    List<Comment> findByItemId(Long itemId, Sort sort);
}
