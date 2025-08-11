package com.example.forum.controller.report.api;

import com.example.forum.controller.report.docs.ReportApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.report.ModerationActionRequest;
import com.example.forum.dto.report.ReportCreateRequest;
import com.example.forum.dto.report.ReportResponse;
import com.example.forum.model.report.ReportStatus;
import com.example.forum.service.report.ReportService;
import com.example.forum.validator.auth.AuthValidator;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@Slf4j
public class ReportController implements ReportApiDocs {

    private final AuthValidator authValidator;
    private final ReportService reportService;

    @Override
    public ResponseEntity<CommonResponse<ReportResponse>> createReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ReportCreateRequest request) {

        String username = authValidator.extractUsername(userDetails);

        log.info("detail={}\n targetUsername={}", request.getDetail(), request.getTargetUsername());

        ReportResponse response = reportService.createReport(username, request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Page<ReportResponse>>> getMyReports(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        String username = authValidator.extractUsername(userDetails);

        Page<ReportResponse> response = reportService.getMyReports(username, pageable);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Page<ReportResponse>>> getModerationQueue(
            @RequestParam ReportStatus status,
            @RequestParam(required = false) Long communityId,
            Pageable pageable) {

        Page<ReportResponse> response = reportService.getModerationQueue(status, communityId, pageable);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> addAttachments(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails moderator,
            @RequestBody List<String> urls) {

        // future feature
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> takeAction(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails moderator,
            @RequestBody @Valid ModerationActionRequest request) {

        reportService.takeAction(id, moderator.getUsername(), request);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> reject(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails moderator,
            @RequestParam(required = false) String note) {

        reportService.reject(id, moderator.getUsername(), note);
        return ResponseEntity.ok(CommonResponse.success());
    }
}
