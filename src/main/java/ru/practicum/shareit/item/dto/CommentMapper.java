package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .id(comment.getId())
                .build();
    }

    public static Comment mapToComment(User user, Item item, CommentDto commentDto, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setCreated(created);
        comment.setText(commentDto.getText());
        comment.setItem(item);
        return comment;
    }
}
