package com.example.forum.validator.comment;

import com.example.forum.exception.auth.UnauthorizedException;
import com.example.forum.exception.comment.CommentNotFoundException;
import com.example.forum.model.comment.Comment;
import com.example.forum.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;

    public Comment validateCommentId(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }

    public void validateCommentAuthor(String expected, String result) {
        if (!expected.equals(result))
            throw new UnauthorizedException();
    }
}
