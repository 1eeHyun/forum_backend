package com.example.forum.service.report;

import com.example.forum.dto.report.ModerationActionRequest;
import com.example.forum.dto.report.ReportCreateRequest;
import com.example.forum.dto.report.ReportResponse;
import com.example.forum.dto.report.ReportTargetType;
import com.example.forum.mapper.report.ReportMapper;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.report.ModerationActionType;
import com.example.forum.model.report.Report;
import com.example.forum.model.report.ReportActionLog;
import com.example.forum.model.report.ReportStatus;
import com.example.forum.model.user.User;
import com.example.forum.repository.report.ActionLogRepository;
import com.example.forum.repository.report.ReportRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.comment.CommentValidator;
import com.example.forum.validator.community.CommunityValidator;
import com.example.forum.validator.post.PostValidator;
import com.example.forum.validator.report.ReportValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final AuthValidator authValidator;
    private final PostValidator postValidator;
    private final CommentValidator commentValidator;
    private final CommunityValidator communityValidator;
    private final ReportValidator reportValidator;

    private final ReportRepository reportRepository;
    private final ActionLogRepository actionLogRepository;

    @Override
    @Transactional
    public ReportResponse createReport(String reporterUsername, ReportCreateRequest req) {

        User reporter = authValidator.validateUserByUsername(reporterUsername);

        reportValidator.isDuplicated(reporter, req.getTargetType(), req.getTargetId());

        Report r = new Report();
        r.setReporter(reporter);
        r.setReason(req.getReason());
        r.setDetail(req.getDetail());
        r.setStatus(ReportStatus.PENDING);
        r.setCreatedAt(Instant.now());
        r.setUpdatedAt(Instant.now());

        // Bind exact one target by type
        switch (req.getTargetType()) {
            case POST -> {
                Post post = postValidator.validatePost(req.getTargetId());
                r.setPost(post);
            }
            case COMMENT -> {
                Comment c = commentValidator.validateCommentId(req.getTargetId());
                r.setComment(c);
            }
            case COMMUNITY -> {
                Community cm = communityValidator.validateExistingCommunity(req.getTargetId());
                r.setCommunity(cm);
            }
            case USER -> {
                // USER Report: Binding
                if (StringUtils.hasText(req.getTargetUsername())) {
                    User targetUser = authValidator.validateUserByUsername(req.getTargetUsername());
                    r.setTargetUser(targetUser);
                } else {
                    User targetUser = authValidator.validateUserById(req.getTargetId()); // validateUserId는 예시
                    r.setTargetUser(targetUser);
                }
            }
        }

        if (r.getTargetUser() == null && StringUtils.hasText(req.getTargetUsername())) {
            User targetUser = authValidator.validateUserByUsername(req.getTargetUsername());
            r.setTargetUser(targetUser);
        }

        r.setSeverity(calcSeverity(req.getTargetType(), req.getTargetId()));

        Report saved = reportRepository.save(r);
        return ReportMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public void addAttachments(Long reportId, List<String> urls, String byModeratorUsername) {

        // future feature
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getModerationQueue(ReportStatus status, Long communityId, Pageable pageable) {

        // If communityId is not null, filter by community target only
        Page<Report> page = (communityId == null)
                ? reportRepository.findByStatusOrderBySeverityDescCreatedAtAsc(status, pageable)
                : reportRepository.findByStatusAndCommunityIdOrderBySeverityDescCreatedAtAsc(status, communityId, pageable);
        return page.map(ReportMapper::toResponse);
    }

    @Override
    @Transactional
    public void takeAction(Long reportId, String moderatorUsername, ModerationActionRequest req) {

        User moderator = authValidator.validateUserByUsername(moderatorUsername);
        Report report = reportValidator.getReportById(reportId);

        // Apply actual moderation side-effects here (delete post/comment, warn user, etc.)
        // NOTE: This part should call corresponding services to perform the action.

        // Log action
        ReportActionLog log = new ReportActionLog();
        log.setReport(report);
        log.setModerator(moderator);
        log.setAction(req.getAction());
        log.setNote(req.getNote());
        log.setCreatedAt(Instant.now());
        actionLogRepository.save(log);

        // Transition state
        report.setStatus(ReportStatus.ACTION_TAKEN);
        report.setUpdatedAt(Instant.now());
        reportRepository.save(report);
    }

    @Override
    @Transactional
    public void reject(Long reportId, String moderatorUsername, String note) {

        User moderator = authValidator.validateUserByUsername(moderatorUsername);
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        ReportActionLog log = new ReportActionLog();
        log.setReport(report);
        log.setModerator(moderator);
        log.setAction(ModerationActionType.NOTE);
        log.setNote(note == null ? "Rejected" : note);
        log.setCreatedAt(Instant.now());
        actionLogRepository.save(log);

        report.setStatus(ReportStatus.REJECTED);
        report.setUpdatedAt(Instant.now());
        reportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getMyReports(String reporterUsername, Pageable pageable) {

        User reporter = authValidator.validateUserByUsername(reporterUsername);
        return reportRepository.findByReporterOrderByCreatedAtDesc(reporter, pageable)
                .map(ReportMapper::toResponse);
    }

    // ------------ Helpers ------------

    private int calcSeverity(ReportTargetType type, Long targetId) {

        // Simple severity based on number of pending/under_review reports for same target
        List<ReportStatus> open = List.of(ReportStatus.PENDING, ReportStatus.UNDER_REVIEW);
        int count = switch (type) {
            case POST -> (int) reportRepository.countByPostIdAndStatusIn(targetId, open);
            case COMMENT -> (int) reportRepository.countByCommentIdAndStatusIn(targetId, open);
            case USER -> (int) reportRepository.countByTargetUserIdAndStatusIn(targetId, open);
            case COMMUNITY -> (int) reportRepository.countByCommunityIdAndStatusIn(targetId, open);
        };

        // Example mapping: 0->0, 1-2->1, 3-5->2, 6+->3
        if (count >= 6) return 3;
        if (count >= 3) return 2;
        if (count >= 1) return 1;
        return 0;
    }
}
