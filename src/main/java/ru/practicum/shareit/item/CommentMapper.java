package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CommentMapper {

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Set<CommentDto> mapToCommentDto(Set<Comment> comments) {
        if (comments == null) {
            return Collections.emptySet();
        }
        Set<CommentDto> commentDtos = new HashSet<>();
        for (Comment comment : comments) {
                commentDtos.add(mapToCommentDto(comment));
        }
        return commentDtos;
    }
}
