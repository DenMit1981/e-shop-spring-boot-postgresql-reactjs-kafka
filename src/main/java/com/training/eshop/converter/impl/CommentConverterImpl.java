package com.training.eshop.converter.impl;

import com.training.eshop.converter.CommentConverter;
import com.training.eshop.dto.comment.CommentDto;
import com.training.eshop.model.Comment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentConverterImpl implements CommentConverter {

    @Override
    public CommentDto convertToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();

        commentDto.setDate(comment.getDate());
        commentDto.setUser(comment.getUser().getName());
        commentDto.setText(comment.getText());

        return commentDto;
    }

    @Override
    public Comment fromCommentDto(CommentDto commentDto) {
        Comment comment = new Comment();

        comment.setText(commentDto.getText());

        return comment;
    }
}
