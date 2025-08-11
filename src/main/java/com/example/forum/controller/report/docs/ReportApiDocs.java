package com.example.forum.controller.report.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.report.ModerationActionRequest;
import com.example.forum.dto.report.ReportCreateRequest;
import com.example.forum.dto.report.ReportResponse;
import com.example.forum.model.report.ReportStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Reports", description = "Endpoints for user reports and moderation")
public interface ReportApiDocs {

    @Operation(
            summary = "Create a report",
            description = "Creates a new report on a post, comment, user, or community.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Report created",
                            content = @Content(schema = @Schema(implementation = ReportResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Duplicate report on the same target", content = @Content)
            }
    )
    @PostMapping
    ResponseEntity<CommonResponse<ReportResponse>> createReport(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Report create request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReportCreateRequest.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "targetType": "POST",
                          "targetId": 123,
                          "reason": "SPAM",
                          "detail": "Repeated spam content",
                          "attachmentUrls": ["https://example.com/proof1.png"]
                        }
                        """
                            )
                    )
            )
            @RequestBody @Valid ReportCreateRequest request
    );

    @Operation(summary = "List my reports")
    @GetMapping("/me")
    ResponseEntity<CommonResponse<Page<ReportResponse>>> getMyReports(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    );

    @Operation(summary = "Get moderation queue")
    @GetMapping("/queue")
    ResponseEntity<CommonResponse<Page<ReportResponse>>> getModerationQueue(
            @RequestParam ReportStatus status,
            @RequestParam(required = false) Long communityId,
            Pageable pageable
    );

    @Operation(summary = "Add attachments to a report (moderator)")
    @PostMapping("/{id}/attachments")
    ResponseEntity<CommonResponse<Void>> addAttachments(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails moderator,
            @RequestBody List<String> urls
    );

    @Operation(summary = "Take moderation action")
    @PostMapping("/{id}/action")
    ResponseEntity<CommonResponse<Void>> takeAction(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails moderator,
            @RequestBody @Valid ModerationActionRequest request
    );

    @Operation(summary = "Reject a report")
    @PostMapping("/{id}/reject")
    ResponseEntity<CommonResponse<Void>> reject(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails moderator,
            @RequestParam(required = false) String note
    );

}
