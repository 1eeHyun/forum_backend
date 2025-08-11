package com.example.forum.dto.report;

import com.example.forum.model.report.ReportReason;
import com.example.forum.model.report.ReportStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {

    private Long id;
    private ReportTargetType targetType;
    private Long targetId;
    private ReportReason reason;
    private String detail;
    private ReportStatus status;
    private int severity;
    private Instant createdAt;
}
