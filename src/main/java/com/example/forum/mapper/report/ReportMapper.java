package com.example.forum.mapper.report;

import com.example.forum.dto.report.ReportResponse;
import com.example.forum.dto.report.ReportTargetType;
import com.example.forum.model.report.Report;

public class ReportMapper {

    public static ReportResponse toResponse(Report r) {
        ReportResponse dto = new ReportResponse();
        dto.setId(r.getId());

        // Set targetType + targetId strictly per target
        if (r.getPost() != null) {
            dto.setTargetType(ReportTargetType.POST);
            dto.setTargetId(r.getPost().getId());

        } else if (r.getComment() != null) {
            dto.setTargetType(ReportTargetType.COMMENT);
            dto.setTargetId(r.getComment().getId());

        } else if (r.getTargetUser() != null) {
            dto.setTargetType(ReportTargetType.USER);
            dto.setTargetId(r.getTargetUser().getId()); // safe here

        } else if (r.getCommunity() != null) {
            dto.setTargetType(ReportTargetType.COMMUNITY);
            dto.setTargetId(r.getCommunity().getId());

        } else {
            // No target bound – defensive: keep nulls or throw if you prefer strictness
            // throw new IllegalStateException("Report target is not set");
        }

        dto.setReason(r.getReason());
        dto.setDetail(r.getDetail());
        dto.setStatus(r.getStatus());
        dto.setSeverity(r.getSeverity());
        dto.setCreatedAt(r.getCreatedAt());

        return dto;
    }
}
