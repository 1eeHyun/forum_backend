package com.example.forum.controller.auth;

import com.example.forum.security.CustomUserDetails;
import com.example.forum.service.auth.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserSessionController {

    private final RedisService redisService;

    @PostMapping("/ping")
    public ResponseEntity<Void> ping(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();
        redisService.markUserOnline(userId);
        return ResponseEntity.ok().build();
    }
}
