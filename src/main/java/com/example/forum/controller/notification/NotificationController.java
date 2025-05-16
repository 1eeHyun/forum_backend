package com.example.forum.controller.notification;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.notification.LinkResponseDTO;
import com.example.forum.dto.notification.NotificationResponseDTO;
import com.example.forum.mapper.notification.NotificationMapper;
import com.example.forum.model.notification.Notification;
import com.example.forum.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApiDocs {

    private final NotificationService notificationService;

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<List<NotificationResponseDTO>>> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails) {

        List<Notification> notifications = notificationService.getMyNotification(userDetails.getUsername());
        List<NotificationResponseDTO> response = notifications.stream()
                .map(NotificationMapper::toDto)
                .toList();

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PostMapping("/read-all")
    public ResponseEntity<CommonResponse<Void>> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {

        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/{notificationId}/resolve")
    public ResponseEntity<CommonResponse<LinkResponseDTO>> resolveLink(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        LinkResponseDTO response = notificationService.resolveAndMarkAsRead(notificationId, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
