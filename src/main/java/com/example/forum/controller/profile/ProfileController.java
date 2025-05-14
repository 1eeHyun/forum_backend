package com.example.forum.controller.profile;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.profile.ProfileResponseDTO;
import com.example.forum.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController implements ProfileApiDocs {

    private final ProfileService profileService;

    @Override
    @GetMapping("/{username}")
    public ResponseEntity<CommonResponse<ProfileResponseDTO>> getProfile(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProfileResponseDTO profile = profileService.getProfile(username, userDetails.getUsername());
        return ResponseEntity.ok(CommonResponse.success(profile));
    }
}
