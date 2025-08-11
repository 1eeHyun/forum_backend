package com.example.forum.service.report;

import com.example.forum.dto.report.ModerationActionRequest;
import com.example.forum.dto.report.ReportCreateRequest;
import com.example.forum.dto.report.ReportResponse;
import com.example.forum.model.report.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {

    ReportResponse createReport(String reporterUsername, ReportCreateRequest req);

    // future feature
    void addAttachments(Long reportId, java.util.List<String> urls, String byModeratorUsername);

    Page<ReportResponse> getModerationQueue(ReportStatus status, Long communityId, Pageable pageable);

    void takeAction(Long reportId, String moderatorUsername, ModerationActionRequest req);

    void reject(Long reportId, String moderatorUsername, String note);

    Page<ReportResponse> getMyReports(String reporterUsername, Pageable pageable);
}
