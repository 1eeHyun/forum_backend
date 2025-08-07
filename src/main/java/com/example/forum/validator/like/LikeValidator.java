package com.example.forum.validator.like;

import com.example.forum.repository.like.CommentLikeRepository;
import com.example.forum.repository.like.PostReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final CommentLikeRepository commentLikeRepository;
    private final PostReactionRepository postLikeRepository;

}
