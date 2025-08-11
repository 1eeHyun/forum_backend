package com.example.forum.model.report;

import com.example.forum.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "report_action_logs",
        indexes = @Index(name = "idx_action_logs_report", columnList = "report_id"))
@Getter
@Setter
public class ReportActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // What action of a report
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ModerationActionType action;

    @Column(length = 1000)
    private String note;  // Reason

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
