package com.example.forum.exception.report;

import com.example.forum.dto.report.ReportTargetType;
import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class DuplicatedReportException extends CustomException {

    public DuplicatedReportException(ReportTargetType type) {
        super(buildMessage(type), 500);
    }

    private static String buildMessage(ReportTargetType type) {
        return switch (type) {
            case POST -> "You have already reported this post.";
            case COMMENT -> "You have already reported this comment.";
            case USER -> "You have already reported this user.";
            case COMMUNITY -> "You have already reported this community.";
        };
    }
}
