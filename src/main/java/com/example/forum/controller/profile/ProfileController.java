package com.example.forum.controller.profile;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.profile.*;
import com.example.forum.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    @PostMapping("/{targetUsername}/nickname")
    public ResponseEntity<CommonResponse<Void>> updateNickname(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NicknameUpdateDTO dto) {

        profileService.updateNickname(targetUsername, userDetails.getUsername(), dto);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PostMapping("/{targetUsername}/username")
    public ResponseEntity<CommonResponse<LoginResponseDTO>> updateUsername(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UsernameUpdateDTO dto) {

        LoginResponseDTO result = profileService.updateUsername(targetUsername, userDetails.getUsername(), dto);
        return ResponseEntity.ok(CommonResponse.success(result));
    }

    @Override
    @PostMapping("/{targetUsername}/bio")
    public ResponseEntity<CommonResponse<Void>> updateBio(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BioUpdateDTO dto) {

        profileService.updateBio(targetUsername, userDetails.getUsername(), dto);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PostMapping(value = "/{targetUsername}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<Void>> updateProfileImage(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam MultipartFile image,
            @RequestParam Double positionX,
            @RequestParam Double positionY) {

        ProfileImageUpdateDTO dto = new ProfileImageUpdateDTO();
        dto.setImage(image);
        dto.setPositionX(positionX);
        dto.setPositionY(positionY);

        profileService.updateProfileImage(targetUsername, userDetails.getUsername(), dto);
        return ResponseEntity.ok(CommonResponse.success());
    }
}
