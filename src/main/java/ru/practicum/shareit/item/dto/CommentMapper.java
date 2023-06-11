package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .id(comment.getId())
                .build();
    }

    public Comment mapToComment(User user, Item item, CommentDto commentDto, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setCreated(created);
        comment.setText(commentDto.getText());
        comment.setItem(item);
        return comment;
    }
}
