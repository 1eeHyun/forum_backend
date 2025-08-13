package com.example.forum.dto.report;

import com.example.forum.model.report.ReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportCreateRequest {

    @NotNull
    private ReportTargetType targetType;

    // For POST/COMMENT/COMMUNITY: required
    // For USER: optional (we'll use targetUsername)
    private Long targetId;

    private String targetUsername;

    @NotNull
    private ReportReason reason;

    @Size(max = 1000)
    private String detail;
}
