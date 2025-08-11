package com.example.forum.repository.report;

import com.example.forum.model.report.Report;
import com.example.forum.model.report.ReportStatus;
import com.example.forum.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndPostId(User reporter, Long postId);
    boolean existsByReporterAndCommentId(User reporter, Long commentId);
    boolean existsByReporterAndTargetUserId(User reporter, Long userId);
    boolean existsByReporterAndCommunityId(User reporter, Long communityId);

    long countByPostIdAndStatusIn(Long postId, List<ReportStatus> statuses);
    long countByCommentIdAndStatusIn(Long commentId, List<ReportStatus> statuses);
    long countByTargetUserIdAndStatusIn(Long userId, List<ReportStatus> statuses);
    long countByCommunityIdAndStatusIn(Long communityId, List<ReportStatus> statuses);

    Page<Report> findByStatusOrderBySeverityDescCreatedAtAsc(ReportStatus status, Pageable pageable);
    Page<Report> findByStatusAndCommunityIdOrderBySeverityDescCreatedAtAsc(ReportStatus status, Long communityId, Pageable pageable);

    Page<Report> findByReporterOrderByCreatedAtDesc(User reporter, Pageable pageable);
}
