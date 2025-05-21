package com.example.forum.controller.profile.api;

import com.example.forum.common.SortOrder;
import com.example.forum.controller.profile.docs.ProfileApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.dto.profile.*;
import com.example.forum.service.post.PostService;
import com.example.forum.service.profile.ProfileService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController implements ProfileApiDocs {

    private final ProfileService profileService;
    private final PostService postService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<ProfileResponseDTO>> getProfile(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {

        String myUsername = authValidator.extractUsername(userDetails);

        ProfileResponseDTO profile = profileService.getProfile(username, myUsername);
        return ResponseEntity.ok(CommonResponse.success(profile));
    }

    @Override
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getProfilePosts(
            @PathVariable String username,
            @RequestParam String sort,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        SortOrder sortOrder = SortOrder.from(sort);
        String myUsername = authValidator.extractUsername(userDetails);
        List<PostResponseDTO> posts = postService.getProfilePosts(username, myUsername, sortOrder, page, size);

        return ResponseEntity.ok(CommonResponse.success(posts));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> updateNickname(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NicknameUpdateDTO dto) {


        String username = authValidator.extractUsername(userDetails);
        authValidator.validateSameUsername(targetUsername, username);

        profileService.updateNickname(username, dto);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<LoginResponseDTO>> updateUsername(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UsernameUpdateDTO dto) {

        String username = authValidator.extractUsername(userDetails);
        authValidator.validateSameUsername(targetUsername, username);

        LoginResponseDTO result = profileService.updateUsername(username, dto);
        return ResponseEntity.ok(CommonResponse.success(result));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> updateBio(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BioUpdateDTO dto) {

        String username = authValidator.extractUsername(userDetails);
        authValidator.validateSameUsername(targetUsername, username);

        profileService.updateBio(username, dto);
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

        String username = authValidator.extractUsername(userDetails);
        authValidator.validateSameUsername(targetUsername, username);

        ProfileImageUpdateDTO dto = new ProfileImageUpdateDTO();
        dto.setImage(image);
        dto.setPositionX(positionX);
        dto.setPositionY(positionY);

        profileService.updateProfileImage(username, dto);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<ProfileResponseDTO>> getProfileByPublicId(@PathVariable String publicId) {


        return null;
    }
}
