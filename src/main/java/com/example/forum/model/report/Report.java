package com.example.forum.model.report;

import com.example.forum.model.comment.Comment;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "reports",
        indexes = {
                @Index(name = "idx_reports_status_created", columnList = "status, createdAt"),
                @Index(name = "idx_reports_reporter", columnList = "reporter_id"),
                @Index(name = "idx_reports_post", columnList = "post_id"),
                @Index(name = "idx_reports_comment", columnList = "comment_id"),
                @Index(name = "idx_reports_target_user", columnList = "target_user_id"),
                @Index(name = "idx_reports_community", columnList = "community_id")
        },
        uniqueConstraints = {
                // User can report only once
                @UniqueConstraint(
                        name = "uk_reporter_unique_target",
                        columnNames = {"reporter_id","post_id","comment_id","target_user_id","community_id"}
                )
        }
)
@Setter
@Getter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ReportReason reason;

    @Column(length = 1000)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ReportStatus status = ReportStatus.PENDING;

    // System detects serious level
    @Column(nullable = false)
    private int severity = 0;

    // 감사(audit)
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();
}
