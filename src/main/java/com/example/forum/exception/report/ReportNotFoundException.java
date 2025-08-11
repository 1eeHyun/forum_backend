package com.example.forum.exception.report;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class ReportNotFoundException extends CustomException {

    public ReportNotFoundException() {
        super("Report not found", 400);
    }
}
