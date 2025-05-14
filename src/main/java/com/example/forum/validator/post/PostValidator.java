package com.example.forum.validator.post;

import com.example.forum.exception.post.PostNotAuthorException;
import com.example.forum.exception.post.PostNotFoundException;
import com.example.forum.model.post.Post;
import com.example.forum.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final PostRepository postRepository;

    public Post validatePost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    public Post validatePostAuthor(Long id, String username) {
        Post post = validatePost(id);

        if (!post.getAuthor().getUsername().equals(username))
            throw new PostNotAuthorException();

        return post;
    }
}
