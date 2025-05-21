package com.example.forum.service.comment;

import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.mapper.comment.CommentMapper;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.comment.CommentRepository;
import com.example.forum.service.notification.NotificationService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.comment.CommentValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.forum.model.notification.Notification.NotificationType.COMMENT;
import static com.example.forum.model.notification.Notification.NotificationType.REPLY;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentValidator commentValidator;
    private final CommentRepository commentRepository;
    private final PostValidator postValidator;
    private final AuthValidator userValidator;

    private final NotificationService notificationService;

    @Override
    public CommentResponseDTO createComment(String username, CommentRequestDTO dto) {

        Post post = postValidator.validatePost(dto.getPostId());
        User user = userValidator.validateUserByUsername(username);

        Comment parent = null;
        if (dto.getParentCommentId() != null)
            parent = commentValidator.validateCommentId(dto.getParentCommentId());

        Comment comment = Comment.builder()
                .post(post)
                .author(user)
                .content(dto.getContent())
                .parentComment(parent)
                .build();

        Comment saved = commentRepository.save(comment);

        User postAuthor = post.getAuthor();
        notificationService.sendNotification(
                postAuthor.getUsername(),
                user.getUsername(),
                COMMENT,
                post.getId(),
                comment,
                user.getProfile().getNickname() + " commented on your post."
        );

        return CommentMapper.toDTO(saved);
    }

    @Override
    public CommentResponseDTO createReply(String username, CommentRequestDTO dto) {

        User user = userValidator.validateUserByUsername(username);
        Comment parent = commentValidator.validateCommentId(dto.getParentCommentId());

        Comment reply = Comment.builder()
                .post(parent.getPost())
                .author(user)
                .parentComment(parent)
                .content(dto.getContent())
                .build();

        Comment saved = commentRepository.save(reply);

        User parentAuthor = parent.getAuthor();

        notificationService.sendNotification(
                parentAuthor.getUsername(),
                user.getUsername(),
                REPLY,
                parent.getPost().getId(),
                saved,
                user.getProfile().getNickname() + " replied to your comment."
        );

        return CommentMapper.toDTO(saved);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {

        postValidator.validatePost(postId);

        List<Comment> topLevelComments = commentRepository.findTopLevelCommentsWithReplies(postId);
        return topLevelComments.stream()
                .map(CommentMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteComment(Long commentId, String username) {

        User user = userValidator.validateUserByUsername(username);

        Comment comment = commentValidator.validateCommentId(commentId);
        User author = comment.getAuthor();

        commentValidator.validateCommentAuthor(author, user);

        comment.getReplies().size();
        commentRepository.delete(comment);
    }
}
