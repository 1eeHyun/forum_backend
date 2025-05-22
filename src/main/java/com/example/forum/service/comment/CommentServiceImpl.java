package com.example.forum.service.comment;

import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.mapper.comment.CommentMapper;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.notification.Notification.NotificationType;
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

    // Validators
    private final CommentValidator commentValidator;
    private final PostValidator postValidator;
    private final AuthValidator userValidator;

    // Repositories
    private final CommentRepository commentRepository;

    // Services
    private final NotificationService notificationService;

    /**
     * This method handles creating a new comment
     * @param username
     * @param dto
     */
    @Override
    public CommentResponseDTO createComment(String username, CommentRequestDTO dto) {

        User user = userValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(dto.getPostId());

        Comment savedComment = createAndSaveComment(user, post, dto.getContent(), null);

        sendNotificationIfNeeded(
                post.getAuthor(),
                user,
                post,
                savedComment,
                COMMENT,
                user.getProfile().getNickname() + " commented on your post."
        );

        return CommentMapper.toDTO(savedComment);
    }

    /**
     *
     * @param username
     * @param dto
     * @return
     */
    @Override
    public CommentResponseDTO createReply(String username, CommentRequestDTO dto) {

        User user = userValidator.validateUserByUsername(username);
        Comment parent = commentValidator.validateCommentId(dto.getParentCommentId());

        Comment savedReply = createAndSaveComment(user, parent.getPost(), dto.getContent(), parent);

        sendNotificationIfNeeded(
                parent.getAuthor(),
                user,
                parent.getPost(),
                savedReply,
                REPLY,
                user.getProfile().getNickname() + " replied to your comment."
        );

        return CommentMapper.toDTO(savedReply);
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

    /**
     * This method helps create and save a comment
     * @return Comment entity object
     */
    private Comment createAndSaveComment(User user, Post post, String content, Comment parent) {
        return commentRepository.save(
                Comment.builder()
                        .post(post)
                        .author(user)
                        .content(content)
                        .parentComment(parent)
                        .build()
        );
    }

    private void sendNotificationIfNeeded(User receiver, User sender, Post post, Comment comment, NotificationType type, String message) {
        if (!receiver.equals(sender)) {
            notificationService.sendNotification(
                    receiver.getUsername(),
                    sender.getUsername(),
                    type,
                    post.getId(),
                    comment,
                    message
            );
        }
    }
}
