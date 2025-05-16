package com.example.forum.controller.notification;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.notification.LinkResponseDTO;
import com.example.forum.dto.notification.NotificationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Tag(name = "Notification", description = "Notification related API")
public interface NotificationApiDocs {


    @Operation(
            summary = "Retrieve a user's notifications",
            description = "Retrieves every notification that a user has received."
    )
    ResponseEntity<CommonResponse<List<NotificationResponseDTO>>> getMyNotifications(UserDetails userDetails);

    @Operation(
            summary = "Mark notifications as read",
            description = "Marks every notification that a user received as read."
    )
    ResponseEntity<CommonResponse<Void>> markAllAsRead(UserDetails userDetails);

    @Operation(
            summary = "Send a notification's link for a user to see",
            description = "Sends a notification's link for a user to see and marks the notification as read."
    )
    ResponseEntity<CommonResponse<LinkResponseDTO>> resolveLink(Long notificationId, UserDetails userDetails);
}
