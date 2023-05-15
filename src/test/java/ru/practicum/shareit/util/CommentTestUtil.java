package ru.practicum.shareit.util;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.UserTestUtil.USER_NAME;

public class CommentTestUtil {
    public static final Long COMMENT_ID = 1L;
    public static final String COMMENT_TEXT = "CommentText";

    public static Comment getComment() {
        return new Comment(COMMENT_ID, COMMENT_TEXT, ItemTestUtil.getItem(), UserTestUtil.getUser(), LocalDateTime.now());
    }

    public static CommentDto getCommentDto() {
        //return new CommentDto(null, COMMENT_TEXT, USER_NAME, LocalDateTime.now());
        return CommentDto.builder()
                .text(COMMENT_TEXT)
                .authorName(USER_NAME)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto getOutputCommentDto() {
        return CommentMapper.toCommentDto(getComment());
    }
}

