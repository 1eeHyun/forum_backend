package com.example.forum.controller.notification.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.notification.LinkResponseDTO;
import com.example.forum.dto.notification.NotificationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Tag(name = "Notification", description = "Notification related API")
public interface NotificationApiDocs {

    @Operation(
            summary = "Get my notifications",
            description = "Retrieves the list of notifications for the currently logged-in user, ordered by newest first.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved notifications",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotificationResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    )
            }
    )
    @GetMapping
    ResponseEntity<CommonResponse<List<NotificationResponseDTO>>> getMyNotifications(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all notifications for the currently logged-in user as read.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All notifications marked as read successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    )
            }
    )
    @PostMapping("/read-all")
    ResponseEntity<CommonResponse<Void>> markAllAsRead(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Resolve a notification",
            description = "Marks the notification as read and returns a link to the relevant target (e.g., post or comment).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Notification marked as read and link resolved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LinkResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user is not the recipient of this notification",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Notification not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{notificationId}/resolve")
    ResponseEntity<CommonResponse<LinkResponseDTO>> resolveLink(
            @Parameter(description = "ID of the notification to resolve", required = true)
            @PathVariable Long notificationId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

}
