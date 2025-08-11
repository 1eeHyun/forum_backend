package com.example.forum.validator.report;

import com.example.forum.dto.report.ReportTargetType;
import com.example.forum.exception.report.DuplicatedReportException;
import com.example.forum.exception.report.ReportNotFoundException;
import com.example.forum.model.report.Report;
import com.example.forum.model.user.User;
import com.example.forum.repository.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportValidator {

    private final ReportRepository reportRepository;

    public Report getReportById(Long reportId) {

        return reportRepository.findById(reportId)
                .orElseThrow(ReportNotFoundException::new);
    }

    public void isDuplicated(User reporter, ReportTargetType type, Long targetId) {

        boolean exists = switch (type) {
            case POST      -> reportRepository.existsByReporterAndPostId(reporter, targetId);
            case COMMENT   -> reportRepository.existsByReporterAndCommentId(reporter, targetId);
            case USER      -> reportRepository.existsByReporterAndTargetUserId(reporter, targetId);
            case COMMUNITY -> reportRepository.existsByReporterAndCommunityId(reporter, targetId);
        };

        if (exists)
            throw new DuplicatedReportException(type);
    }
}
